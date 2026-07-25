#!/usr/bin/env python3
import json
import sys


def strings(value):
    if isinstance(value, dict):
        for nested in value.values():
            yield from strings(nested)
    elif isinstance(value, list):
        for nested in value:
            yield from strings(nested)
    elif isinstance(value, str):
        yield value


def main():
    with open(sys.argv[1], encoding="utf-8") as source:
        matrix = json.load(source)

    screenshot_uris = {
        value
        for value in strings(matrix)
        if value.startswith("gs://") and value.lower().endswith(".png")
    }
    for uri in sorted(screenshot_uris):
        print(uri)


if __name__ == "__main__":
    main()
