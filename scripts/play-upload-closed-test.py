#!/data/data/com.termux/files/usr/bin/env python3
import argparse
import pathlib
import sys


ANDROID_PUBLISHER_SCOPE = "https://www.googleapis.com/auth/androidpublisher"
DEFAULT_PACKAGE_NAME = "io.github.yosk.mdlite"
DEFAULT_AAB = (
    "~/AndroidDev/releases/mdlite-reader/v0.1.0/"
    "mdlite-reader-0.1.0-release.aab"
)


def load_google_api():
    try:
        from google.oauth2 import service_account
        from googleapiclient.discovery import build
        from googleapiclient.http import MediaFileUpload
    except ImportError as exc:
        raise SystemExit(
            "Missing Google API dependencies. Run:\n"
            "  python3 -m pip install --user -r requirements-play-api.txt"
        ) from exc
    return service_account, build, MediaFileUpload


def release_notes():
    return [
        {
            "language": "en-US",
            "text": (
                "Initial closed test release of MdLite Reader v0.1.0. "
                "Includes local Markdown viewing, tabs, recent files, "
                "light/dark themes, pinch font sizing, and offline behavior."
            ),
        },
        {
            "language": "ja-JP",
            "text": (
                "MdLite Reader v0.1.0 の初回クローズドテストリリースです。"
                "ローカルMarkdown表示、タブ、最近開いたファイル、ライト/"
                "ダークテーマ、ピンチでのフォントサイズ変更、オフライン動作を"
                "含みます。"
            ),
        },
    ]


def parse_args():
    parser = argparse.ArgumentParser(
        description="Upload MdLite Reader AAB to a Google Play testing track."
    )
    parser.add_argument(
        "--service-account",
        required=True,
        help="Path to the Google Play service account JSON file.",
    )
    parser.add_argument(
        "--package-name",
        default=DEFAULT_PACKAGE_NAME,
        help=f"Android package name. Default: {DEFAULT_PACKAGE_NAME}",
    )
    parser.add_argument(
        "--aab",
        default=DEFAULT_AAB,
        help=f"Signed AAB path. Default: {DEFAULT_AAB}",
    )
    parser.add_argument(
        "--track",
        required=True,
        help=(
            "Google Play track identifier, for example alpha, beta, internal, "
            "or a custom closed testing track id."
        ),
    )
    parser.add_argument(
        "--status",
        choices=["draft", "completed"],
        default="draft",
        help="Release status to create. Default: draft.",
    )
    parser.add_argument(
        "--name",
        default="MdLite Reader v0.1.0 closed test",
        help="Release name shown in Play Console.",
    )
    parser.add_argument(
        "--validate-only",
        action="store_true",
        help="Validate the edit instead of committing it.",
    )
    return parser.parse_args()


def main():
    args = parse_args()
    aab_path = pathlib.Path(args.aab).expanduser()
    account_path = pathlib.Path(args.service_account).expanduser()

    if not aab_path.is_file():
        raise SystemExit(f"Missing AAB: {aab_path}")
    if not account_path.is_file():
        raise SystemExit(f"Missing service account JSON: {account_path}")

    service_account, build, MediaFileUpload = load_google_api()
    credentials = service_account.Credentials.from_service_account_file(
        str(account_path),
        scopes=[ANDROID_PUBLISHER_SCOPE],
    )
    publisher = build(
        "androidpublisher",
        "v3",
        credentials=credentials,
        cache_discovery=False,
    )

    package_name = args.package_name
    edit = publisher.edits().insert(packageName=package_name).execute()
    edit_id = edit["id"]
    print(f"Created edit: {edit_id}")

    media = MediaFileUpload(
        str(aab_path),
        mimetype="application/octet-stream",
        resumable=True,
    )
    bundle = (
        publisher.edits()
        .bundles()
        .upload(
            packageName=package_name,
            editId=edit_id,
            media_body=media,
        )
        .execute()
    )
    version_code = str(bundle["versionCode"])
    print(f"Uploaded bundle versionCode: {version_code}")

    track_body = {
        "track": args.track,
        "releases": [
            {
                "name": args.name,
                "versionCodes": [version_code],
                "status": args.status,
                "releaseNotes": release_notes(),
            }
        ],
    }
    publisher.edits().tracks().update(
        packageName=package_name,
        editId=edit_id,
        track=args.track,
        body=track_body,
    ).execute()
    print(f"Updated track: {args.track} ({args.status})")

    if args.validate_only:
        publisher.edits().validate(
            packageName=package_name,
            editId=edit_id,
        ).execute()
        print("Validated edit without commit.")
        return

    publisher.edits().commit(
        packageName=package_name,
        editId=edit_id,
    ).execute()
    print("Committed edit.")


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        raise
