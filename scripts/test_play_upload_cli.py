import importlib.util
import pathlib
import unittest
from unittest.mock import patch


SCRIPT = pathlib.Path(__file__).with_name("play-upload-closed-test.py")
SPEC = importlib.util.spec_from_file_location("play_upload", SCRIPT)
MODULE = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(MODULE)


class ExistingBundlePromotionTest(unittest.TestCase):
    def test_existing_version_code_selects_promotion_without_an_aab(self):
        with patch("sys.argv", ["play-upload", "--track", "beta", "--version-code", "33"]):
            args = MODULE.parse_args()

        self.assertEqual("33", args.version_code)
        self.assertIsNone(args.aab)

    def test_aab_upload_and_existing_version_promotion_are_mutually_exclusive(self):
        argv = [
            "play-upload",
            "--track", "beta",
            "--aab", "release.aab",
            "--version-code", "33",
        ]

        with patch("sys.argv", argv), self.assertRaises(SystemExit):
            MODULE.parse_args()


if __name__ == "__main__":
    unittest.main()
