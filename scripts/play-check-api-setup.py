#!/data/data/com.termux/files/usr/bin/env python3
import argparse
import pathlib
import sys


DEFAULT_SERVICE_ACCOUNT = "~/AndroidDev/secrets/google-play-service-account.json"
DEFAULT_AAB = (
    "~/AndroidDev/releases/mdlite-reader/v0.1.0/"
    "mdlite-reader-0.1.0-release.aab"
)


def import_status(module_name):
    try:
        __import__(module_name)
        return True
    except Exception:
        return False


def parse_args():
    parser = argparse.ArgumentParser(
        description="Check local Google Play Developer API setup."
    )
    parser.add_argument(
        "--service-account",
        default=DEFAULT_SERVICE_ACCOUNT,
        help=f"Service account JSON path. Default: {DEFAULT_SERVICE_ACCOUNT}",
    )
    parser.add_argument(
        "--aab",
        default=DEFAULT_AAB,
        help=f"Signed AAB path. Default: {DEFAULT_AAB}",
    )
    return parser.parse_args()


def main():
    args = parse_args()
    ok = True

    modules = [
        "google.oauth2.service_account",
        "googleapiclient.discovery",
        "googleapiclient.http",
    ]
    for module in modules:
        exists = import_status(module)
        print(f"{module}: {'ok' if exists else 'missing'}")
        ok = ok and exists

    service_account = pathlib.Path(args.service_account).expanduser()
    aab = pathlib.Path(args.aab).expanduser()

    print(f"service account: {service_account}")
    if service_account.is_file():
        print("service account file: ok")
    else:
        print("service account file: missing")
        ok = False

    print(f"aab: {aab}")
    if aab.is_file():
        print("aab file: ok")
    else:
        print("aab file: missing")
        ok = False

    if not ok:
        print("setup: incomplete", file=sys.stderr)
        return 1

    print("setup: ok")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
