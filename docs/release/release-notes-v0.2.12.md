# LocalMD Reader v0.2.12

Release date: 2026-07-25

## Highlights

- Replaced recent-file, pinned-file, theme, and gesture list dialogs with inline expandable menu panels.
- Placed every expandable panel directly below its owning action for clearer navigation.
- Distinguished gesture actions already assigned to another gesture while keeping reassignment available.
- Preserved the correct accessibility action after changing appearance settings with the menu open.

## Compatibility

- Minimum supported Android version remains Android 6.0 (API 23).
- No new permissions, tracking, account, or network access were added.

## Verification

- All 923 unit tests, the local Android build, and the required CI checks passed.
- The updated menu, theme, gesture, recent-file, and pinned-file flows were verified on an Android device.
