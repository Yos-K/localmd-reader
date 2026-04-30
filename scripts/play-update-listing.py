#!/data/data/com.termux/files/usr/bin/env python3
import argparse
import pathlib
import sys


ANDROID_PUBLISHER_SCOPE = "https://www.googleapis.com/auth/androidpublisher"
DEFAULT_PACKAGE_NAME = "io.github.yosk.mdlite"
DEFAULT_SERVICE_ACCOUNT = "~/AndroidDev/secrets/google-play-service-account.json"


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


def read_text(path):
    return pathlib.Path(path).read_text(encoding="utf-8").strip()


def listing_body(root, locale):
    listing_dir = root / "play-store" / "listing" / locale
    return {
        "title": read_text(listing_dir / "title.txt"),
        "shortDescription": read_text(listing_dir / "short-description.txt"),
        "fullDescription": read_text(listing_dir / "full-description.txt"),
    }


def upload_single_image(
    publisher,
    media_file_upload,
    package_name,
    edit_id,
    locale,
    image_type,
    path,
):
    publisher.edits().images().deleteall(
        packageName=package_name,
        editId=edit_id,
        language=locale,
        imageType=image_type,
    ).execute()

    media = media_file_upload(
        str(path),
        mimetype="image/png",
        resumable=True,
    )
    publisher.edits().images().upload(
        packageName=package_name,
        editId=edit_id,
        language=locale,
        imageType=image_type,
        media_body=media,
    ).execute()


def parse_args():
    parser = argparse.ArgumentParser(
        description="Update Google Play store listing text and primary images."
    )
    parser.add_argument(
        "--service-account",
        default=DEFAULT_SERVICE_ACCOUNT,
        help=f"Service account JSON path. Default: {DEFAULT_SERVICE_ACCOUNT}",
    )
    parser.add_argument(
        "--package-name",
        default=DEFAULT_PACKAGE_NAME,
        help=f"Android package name. Default: {DEFAULT_PACKAGE_NAME}",
    )
    parser.add_argument(
        "--locale",
        action="append",
        default=["en-US", "ja-JP"],
        help="Listing locale to update. Can be repeated. Default: en-US and ja-JP.",
    )
    parser.add_argument(
        "--validate-only",
        action="store_true",
        help="Validate the edit instead of committing it.",
    )
    return parser.parse_args()


def main():
    args = parse_args()
    root = pathlib.Path(__file__).resolve().parents[1]
    account_path = pathlib.Path(args.service_account).expanduser()
    icon_path = root / "play-store" / "icon-512.png"
    feature_graphic_path = root / "play-store" / "feature-graphic-1024x500.png"

    if not account_path.is_file():
        raise SystemExit(f"Missing service account JSON: {account_path}")
    if not icon_path.is_file():
        raise SystemExit(f"Missing app icon: {icon_path}")
    if not feature_graphic_path.is_file():
        raise SystemExit(f"Missing feature graphic: {feature_graphic_path}")

    service_account, build, media_file_upload = load_google_api()
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

    for locale in args.locale:
        body = listing_body(root, locale)
        publisher.edits().listings().update(
            packageName=package_name,
            editId=edit_id,
            language=locale,
            body=body,
        ).execute()
        print(f"Updated listing text: {locale}")

        upload_single_image(
            publisher,
            media_file_upload,
            package_name,
            edit_id,
            locale,
            "icon",
            icon_path,
        )
        print(f"Uploaded app icon: {locale}")

        upload_single_image(
            publisher,
            media_file_upload,
            package_name,
            edit_id,
            locale,
            "featureGraphic",
            feature_graphic_path,
        )
        print(f"Uploaded feature graphic: {locale}")

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
