# Closed Testing Guide

This guide defines the closed testing workflow for LocalMD Reader v0.1.0.

## Goal

Use Google Play closed testing to satisfy the production access requirement and
to confirm that the first Play-distributed build works on real devices.

## Requirements

For a new personal Google Play developer account, production access may require:

- A closed testing track.
- At least 12 testers opted in.
- Testers remain opted in for at least 14 continuous days.
- A production access application after the testing period.

Internal testing does not satisfy this requirement.

## Test Build

Upload the signed release AAB to the closed testing track:

```text
~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
```

If the AAB was rebuilt after package ownership verification, stage it again:

```sh
mkdir -p ~/AndroidDev/releases/mdlite-reader/v0.1.0
cp build/release/mdlite-reader-0.1.0-release.aab ~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab
sha256sum ~/AndroidDev/releases/mdlite-reader/v0.1.0/mdlite-reader-0.1.0-release.aab > ~/AndroidDev/releases/mdlite-reader/v0.1.0/SHA256SUMS
```

## Tester Setup

Recommended tester list:

- Use a Google Group or email list dedicated to LocalMD Reader testing.
- Add at least 12 testers before starting the 14-day period.
- Ask testers to opt in from the Play testing link.
- Ask testers not to opt out until the 14-day period is complete.

## Feedback Channel

Use one simple feedback channel:

Use GitHub Issues:

```text
https://github.com/Yos-K/localmd-reader/issues
```

Do not ask testers to send private Markdown files. Ask for short descriptions
or screenshots that do not include personal information.

## Test Scope

Ask testers to check:

- The app installs from Google Play.
- The app launches.
- A local `.md` or `.markdown` file opens.
- Text, headings, lists, links, tables, and code blocks are readable.
- Tabs can be opened and closed.
- Light and dark theme switching works.
- Font size changes with pinch.
- The privacy dialog opens.

## Production Access Notes

Keep notes during the 14-day period:

- Number of opted-in testers.
- Start date and end date.
- Devices or Android versions tested.
- Issues found and fixes made.
- Why the app is ready for production.

These notes can be used when applying for production access.
