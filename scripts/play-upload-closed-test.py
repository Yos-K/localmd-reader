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
        import google.auth
        from googleapiclient.discovery import build
        from googleapiclient.http import MediaFileUpload
    except ImportError as exc:
        raise SystemExit(
            "Missing Google API dependencies. Run:\n"
            "  python3 -m pip install --user -r requirements-play-api.txt"
        ) from exc
    return google.auth, build, MediaFileUpload


_RELEASE_NOTES_DIR = (
    pathlib.Path(__file__).parent.parent / "play-store" / "release-notes"
)
_LOCALES = ("en-US", "ja-JP")


def release_notes():
    notes = []
    for locale in _LOCALES:
        notes_file = _RELEASE_NOTES_DIR / locale / "whatsnew.txt"
        if not notes_file.exists():
            raise FileNotFoundError(
                f"Release notes file not found: {notes_file}"
            )
        notes.append({"language": locale, "text": notes_file.read_text(encoding="utf-8")})
    return notes


def parse_args():
    parser = argparse.ArgumentParser(
        description="Upload LocalMD Reader AAB to a Google Play testing track."
    )
    parser.add_argument(
        "--service-account",
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
        default="LocalMD Reader v0.1.0 closed test",
        help="Release name shown in Play Console.",
    )
    parser.add_argument(
        "--validate-only",
        action="store_true",
        help="Validate the edit instead of committing it.",
    )
    parser.add_argument(
        "--changes-not-sent-for-review",
        action="store_true",
        help="Commit the edit without sending changes for review automatically.",
    )
    return parser.parse_args()


def load_credentials(google_auth, service_account_path):
    if service_account_path:
        try:
            from google.oauth2 import service_account
        except ImportError as exc:
            raise SystemExit(
                "Missing Google service account dependency. Run:\n"
                "  python3 -m pip install --user -r requirements-play-api.txt"
            ) from exc
        account_path = pathlib.Path(service_account_path).expanduser()
        if not account_path.is_file():
            raise SystemExit(f"Missing service account JSON: {account_path}")
        return service_account.Credentials.from_service_account_file(
            str(account_path),
            scopes=[ANDROID_PUBLISHER_SCOPE],
        )
    credentials, _ = google_auth.default(scopes=[ANDROID_PUBLISHER_SCOPE])
    return credentials


def main():
    args = parse_args()
    aab_path = pathlib.Path(args.aab).expanduser()

    if not aab_path.is_file():
        raise SystemExit(f"Missing AAB: {aab_path}")

    google_auth, build, MediaFileUpload = load_google_api()
    credentials = load_credentials(google_auth, args.service_account)
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

    commit_args = {
        "packageName": package_name,
        "editId": edit_id,
    }
    if args.changes_not_sent_for_review:
        commit_args["changesNotSentForReview"] = True
    publisher.edits().commit(**commit_args).execute()
    print("Committed edit.")


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        raise
