# Play Store Data Safety

This document defines the Google Play Data safety declaration plan for MdLite
Reader v0.1.0.

Official requirements checked on 2026-04-15:

- Google Play Data safety form:
  https://support.google.com/googleplay/android-developer/answer/10787469
- Google Play User Data policy:
  https://support.google.com/googleplay/android-developer/answer/10144311

## App Summary

LocalMD Reader v0.1.0 is a local-first Android Markdown viewer.

The app:

- Has no ads.
- Has no analytics SDK.
- Has no login or account.
- Has no automatic crash reporting.
- Requests no `android.permission.INTERNET` permission.
- Does not upload Markdown content.
- Does not transmit file names, file paths, Android document URIs, or recent file metadata off device.

## Data Safety Form Answers

Data collection and security:

```text
Does your app collect or share any of the required user data types?
No
```

Rationale:

Google Play defines collection as transmitting user data from the app off the
user's device. LocalMD Reader v0.1.0 does not request network access and does not
transmit user data.

Data sharing:

```text
Does your app share user data with other companies or organizations?
No
```

Rationale:

LocalMD Reader v0.1.0 does not transmit user data to the developer, third-party
services, SDKs, or other organizations.

Data types:

```text
No data types selected
```

Rationale:

The app does not collect or share any required Google Play user data type.

Data encryption in transit:

```text
Not applicable
```

Rationale:

The app does not collect or transmit user data.

Data deletion request mechanism:

```text
Not applicable for collected data
```

Rationale:

The app does not collect data. Local app-private preferences can be removed by
clearing app data. Recent file history can also be cleared from inside the app.

## Local Data That Is Not Collected

The app stores small local preferences in app-private storage:

- Theme setting.
- Controls placement setting.
- UI language setting.
- Recently opened file metadata, limited to 5 entries.
- Open tab metadata for tab restoration.

Recent file and tab metadata may include a display name and Android document
URI. This stays on the device and is not transmitted by the app.

## Privacy Policy URL

Use this public privacy policy URL:

```text
https://gist.github.com/Yos-K/23b876101848591692bc94a5f92dd024
```

Release requirement:

- The repository must remain private during development.
- The privacy policy URL must remain publicly accessible during Play Store
  submission and after release.

## In-App Privacy Text

No prominent disclosure or consent dialog is required for v0.1.0 because the
app does not collect, transmit, sync, sell, or share personal or sensitive user
data.

The app provides local privacy text from the hamburger menu:

```text
Menu -> Privacy
```

This dialog is local text. It does not open a network URL because v0.1.0 does
not request the `INTERNET` permission.

## Change Control

Update this document, `PRIVACY.md`, `PRIVACY.ja.md`, and the Play Console Data
safety form before releasing any future version that adds:

- Network access.
- Analytics.
- Ads.
- Login or accounts.
- Sync.
- Crash reporting.
- Remote image loading.
- Cloud storage integration.
- Any SDK that collects or shares user data.
