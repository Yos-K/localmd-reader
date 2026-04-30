# Play Console Submission Guide

This guide lists the Google Play Console fields that must be entered manually
for LocalMD Reader v0.1.0.

## App Listing

Use these files for the English listing:

- `play-store/listing/en-US/title.txt`
- `play-store/listing/en-US/short-description.txt`
- `play-store/listing/en-US/full-description.txt`

Use these files for the Japanese listing:

- `play-store/listing/ja-JP/title.txt`
- `play-store/listing/ja-JP/short-description.txt`
- `play-store/listing/ja-JP/full-description.txt`

Primary category:

```text
Productivity
```

## Graphic Assets

Upload these assets:

- `play-store/icon-512.png`
- `play-store/feature-graphic-1024x500.png`
- `play-store/screenshots/phone-01-welcome.jpg`
- `play-store/screenshots/phone-02-document.jpg`
- `play-store/screenshots/phone-03-dark-table.jpg`
- `play-store/screenshots/phone-04-tabs-menu.jpg`

## Alt Text

Enter the English feature graphic alt text from:

```text
play-store/listing/en-US/feature-graphic-alt.txt
```

Enter the English screenshot alt text from:

```text
play-store/listing/en-US/phone-screenshot-01-alt.txt
play-store/listing/en-US/phone-screenshot-02-alt.txt
play-store/listing/en-US/phone-screenshot-03-alt.txt
play-store/listing/en-US/phone-screenshot-04-alt.txt
```

Enter the Japanese feature graphic alt text from:

```text
play-store/listing/ja-JP/feature-graphic-alt.txt
```

Enter the Japanese screenshot alt text from:

```text
play-store/listing/ja-JP/phone-screenshot-01-alt.txt
play-store/listing/ja-JP/phone-screenshot-02-alt.txt
play-store/listing/ja-JP/phone-screenshot-03-alt.txt
play-store/listing/ja-JP/phone-screenshot-04-alt.txt
```

## Privacy Policy URL

Use this public privacy policy URL:

```text
https://gist.github.com/Yos-K/23b876101848591692bc94a5f92dd024
```

The page includes both English and Japanese text.

## Data Safety

Use `docs/play-store-data-safety.md` as the source of truth.

Answers:

```text
Does your app collect or share any of the required user data types?
No

Does your app share user data with other companies or organizations?
No

Data types:
No data types selected

Data encryption in transit:
Not applicable

Data deletion request mechanism:
Not applicable for collected data
```

Reason:

LocalMD Reader v0.1.0 has no ads, analytics SDK, login, automatic crash
reporting, or `android.permission.INTERNET` permission. It does not transmit
Markdown content, file names, file paths, Android document URIs, or recent file
metadata off device.

## Closed Testing

Use `docs/closed-testing-guide.md` for the closed testing workflow.
Use `docs/play-developer-api.md` when uploading the AAB through the Google Play
Developer API.

Recommended tester invitation:

```text
play-store/testing/closed-test-invitation.txt
```

Recommended feedback template:

```text
play-store/testing/closed-test-feedback-template.txt
```

Closed testing should use the signed release AAB:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

## Before Submission

- Confirm the privacy policy URL opens in a private browser session.
- Confirm the uploaded screenshots do not show personal information.
- Confirm the uploaded AAB is the latest signed release build.
- Confirm Data safety answers still match the APK/AAB permissions.
- Confirm the app remains private in the repository until the public release
  decision is made.

## Package Name Verification Token

If Play Console says that the uploaded APK or AAB is missing the required token
file, copy the snippet shown by Play Console into this local file:

```text
src/main/assets/adi-registration.properties
```

Then rebuild and upload the release AAB again:

```sh
scripts/build-release-aab.sh
```

The token file is intentionally ignored by Git. Do not commit it.
