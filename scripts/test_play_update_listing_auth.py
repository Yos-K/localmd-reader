import importlib.util
import pathlib
import tempfile
import unittest
from unittest.mock import Mock


SCRIPT = pathlib.Path(__file__).with_name("play-update-listing.py")
SPEC = importlib.util.spec_from_file_location("play_update_listing", SCRIPT)
MODULE = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(MODULE)


class ListingAuthenticationTest(unittest.TestCase):
    def test_workload_identity_credential_file_is_loaded_by_google_auth(self):
        google_auth = Mock()
        credentials = object()
        google_auth.load_credentials_from_file.return_value = (credentials, "project")

        with tempfile.NamedTemporaryFile() as credential_file:
            actual = MODULE.load_credentials(google_auth, credential_file.name)

        self.assertIs(credentials, actual)
        google_auth.load_credentials_from_file.assert_called_once_with(
            credential_file.name,
            scopes=[MODULE.ANDROID_PUBLISHER_SCOPE],
        )


if __name__ == "__main__":
    unittest.main()
