# ADR-0021: Run Advisory Pro Exploration on Cloud Physical Devices

Status: Accepted

## Decision

Run Pro UI exploration as a manually dispatched Firebase Test Lab Robo test on
one physical Android device. Build the unsigned `ProPreviewDebug` variant with
Gradle, authenticate from GitHub Actions through Workload Identity Federation,
and use a dedicated Test Lab service account and result bucket.

Physical-device exploration is advisory rather than a required merge gate. Each
run has a 5, 10, or 15 minute limit. Test Lab keeps the complete result, while
GitHub Actions retains matrix metadata, logs, and at most twelve screenshots for
one day so an agent can review the behavior without consuming persistent Actions
storage. The dedicated Cloud Storage bucket deletes objects after seven days.

## Alternatives Considered

- Drive the owner's phone from Termux through wireless ADB.
- Continue using only headless GitHub Actions emulators.
- Run a physical-device matrix on every pull request.
- Upload a signed Play build and rely only on Play pre-launch reports.
- Use a manually dispatched physical Robo test with a dedicated cloud identity.

## Why This Decision

A cloud physical device does not occupy the owner's screen and exercises real
hardware, vendor WebView integration, system UI, and Android behavior that can
differ from the existing emulator. Robo exploration requires no duplicate UI
test suite, is repeatable for the same device configuration, and provides logs,
screenshots, and video for agent triage. Manual dispatch and one bounded device
keep the slow, flaky, and billable large-test layer proportional to its value.

The Pro preview variant exposes Pro behavior without release signing material or
a Play purchase. A dedicated keyless identity preserves the public/private
release boundary in ADR-0014 and limits compromise impact.

## Why Alternatives Were Rejected

Wireless ADB takes over the owner's visible phone and is unavailable when the
device is disconnected. Emulator-only testing cannot establish physical-device
confidence. Running billable physical tests for every pull request is slow,
flaky, and contrary to the test pyramid. Play pre-launch reports do not provide
the same controllable Pro-preview entry point and are tied to uploaded releases.

## Reconsider When

Reconsider when physical runs produce no unique findings for three consecutive
release cycles, Test Lab pricing or availability changes materially, Robo cannot
reach important WebView or gesture paths, or stable instrumentation tests can
replace the explored paths with stronger assertions at acceptable cost.
