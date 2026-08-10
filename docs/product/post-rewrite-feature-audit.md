# Post-Rewrite Feature Audit

## Purpose

Verify that user-facing behavior from the final tree before the public-history
rewrite still exists in the current source and regression tests. Commit IDs
cannot be compared across the unrelated histories, so the audit compares the
old final tree, current implementation, and tests by capability.

## Results

| Capability | Status | Evidence or action |
|---|---|---|
| Vertical menu scrolling | Retained | `SwipeMenuScrollView` and `MenuPanelScrollabilityTest` remain |
| Persistent Markdown library tree | Reimplemented | ADR-0017 and the current inline menu tree cover it |
| Complete English/Japanese Pro copy | Reimplemented | ADR-0018 and `ProFeatureCatalogTest` cover it |
| Long-press tab pinning | Restored, device-verified | Covered by `TabPinningDecision`, interaction regression tests, and the 2026-08-10 Pro device exploration |
| Pin mark on pinned tabs | Restored, device-verified | Theme-colored `ic_push_pin_18` and immediate visual refresh were verified on device |
| Individual removal from pinned files | Restored, device-verified | Explicit open/unpin state and removal of only the selected pin were verified on device |
| Pin mutation UI synchronization | Restored, device-verified | Shared refresh behavior and immediate tab-marker removal were verified on device |
| Unique gesture action assignments | Restored | Assigning an active action now moves it to the selected gesture |
| Assigned gesture shown in action choices | Restored | Localized action labels identify the current gesture owner |
| Replace a temporary tab after Save As | Restored | Typed placement replaces the source draft and reuses an open destination |
| Markdown bold rendering from clipboard content | Restored | Plain clipboard Markdown remains intact and `**` renders as strong text |
| Raw/Preview for HTML and Markdown code blocks | Restored | Theme-aware switch and restricted safe HTML preview are covered by tests |
| Code-fence metadata for previews | Restored | The normalized first info-string token selects language behavior |

## Restoration Order

1. Complete pinning behavior and verify it with a local APK.
2. Restore replacement of temporary tabs after Save As.
3. Restore Markdown bold rendering.
4. Restore unique gesture actions and assignment labels.
5. Restore Raw/Preview and code-fence metadata as one rendering change.

Each restoration starts with a failing test adapted to the current ownership
model and ADRs. Old implementation code is evidence, not a patch to copy
blindly.
