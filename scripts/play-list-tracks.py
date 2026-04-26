#!/data/data/com.termux/files/usr/bin/env python3
import argparse
import pathlib
import sys


ANDROID_PUBLISHER_SCOPE = "https://www.googleapis.com/auth/androidpublisher"
DEFAULT_PACKAGE_NAME = "io.github.yosk.mdlite"


def load_google_api():
    try:
        from google.oauth2 import service_account
        from googleapiclient.discovery import build
    except ImportError as exc:
        raise SystemExit(
            "Missing Google API dependencies. Run:\n"
            "  python3 -m pip install --user -r requirements-play-api.txt"
        ) from exc
    return service_account, build


def parse_args():
    parser = argparse.ArgumentParser(
        description="List Google Play tracks for MdLite Reader."
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
    return parser.parse_args()


def main():
    args = parse_args()
    account_path = pathlib.Path(args.service_account).expanduser()
    if not account_path.is_file():
        raise SystemExit(f"Missing service account JSON: {account_path}")

    service_account, build = load_google_api()
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
    response = (
        publisher.edits()
        .tracks()
        .list(packageName=package_name, editId=edit_id)
        .execute()
    )

    tracks = response.get("tracks", [])
    if not tracks:
        print("No tracks returned.")
        return

    for track in tracks:
        releases = track.get("releases", [])
        release_count = len(releases)
        print(f"{track.get('track')} releases={release_count}")


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        raise
