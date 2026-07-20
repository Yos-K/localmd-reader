# GitHub Actions CI/CD

This project uses these GitHub Actions workflows: continuous integration
(`ci.yml`), mutation analysis (`mutation.yml`), a manual device smoke test
(`device-smoke.yml`), manual theme screenshots (`theme-screenshots.yml`), and a
manual Play release (`play-release.yml`).

The release model is:

- **Free Play release**: the real Google Play package, `io.github.yosk.mdlite`.
- **Pro preview**: a CI artifact for checking Pro-only behavior. It is not
  uploaded to Google Play as a separate paid app.

This keeps the Play app free while Pro is implemented as an in-app entitlement.

## CI

`.github/workflows/ci.yml` runs on pull requests and pushes to `main`.

The fast `fitness` job runs source-level guards that fail a pull request early,
without an Android build:

```sh
sh scripts/check-file-sizes.sh           # per-file line count limit
sh scripts/check-hard-constraints.sh     # no INTERNET permission, reader WebView keeps JS off
sh scripts/check-no-committed-secrets.sh # no committed keystores, keys, or service-account files
sh scripts/check-docs-currency.sh        # domain/harness/policy changes update the matching docs (unified)
sh scripts/check-unit-test-dependency-downloads.sh # unit-test jar downloads fail on HTTP/truncated jars
```

The `test` job installs Java, Android SDK platform 35, Build Tools 35.0.0, then
runs:

```sh
sh test.sh
```

`scripts/run-unit-tests.sh`, which is called by `test.sh`, downloads the pure JVM
test runner dependencies from Maven Central. Those downloads must use
`curl --fail --retry` and must validate each jar with `jar tf` before compiling
tests. This keeps transient or truncated jar downloads from surfacing later as
ambiguous `zip END header not found` compiler errors.

The `test` job also builds and uploads two separate debug APK artifacts, one per
channel. Each artifact name includes the channel, version name, version code, and
short commit SHA so a reviewer can tell exactly what to install:

- `localmd-reader-free-debug-v<versionName>-<versionCode>-<shortSha>`
- `localmd-reader-pro-preview-debug-v<versionName>-<versionCode>-<shortSha>`

Download them from the CI run page (the **Summary** tab, **Artifacts** section).
Pick the `free` artifact for the Play package build and the `pro-preview` artifact
to check Pro-only behavior.

The `gradle-build` job also verifies:

- Free / Pro Preview unit tests
- Android lint for Free debug
- Free / Pro Preview debug APK builds
- Free release AAB build

### Gradle wrapper

`gradle-build` invokes Gradle through the committed wrapper (`./gradlew`), pinned
to the version in `gradle/wrapper/gradle-wrapper.properties`, instead of a
system-provided `gradle`. This makes the Gradle version reproducible across CI,
local machines, and the Play release workflow.

What remains shell-based (Termux hand-written scripts, intentionally kept and not
retired by this change):

- `test` job: `sh test.sh` (unit tests + quality gates) and `sh build.sh`
  (Free / Pro Preview debug APKs).
- `Play Release` default path: `build_system: script` still produces the signed
  AAB with `scripts/build-signed-release.sh` and bundletool. The Gradle path
  (`build_system: gradle`) is opt-in and uses `./gradlew`; its direct Play upload
  stays gated off until the Gradle-built AAB is verified against the script build.

## Artifacts

CI and the release workflow publish build outputs as GitHub Actions artifacts.
Open the workflow run, then download from the **Artifacts** box at the bottom of
the run summary.

| Artifact name pattern | Output | Built by | When |
|---|---|---|---|
| `localmd-reader-free-debug-v<ver>-<code>-<sha>` | Free debug APK | CI, `test` job | every PR / push |
| `localmd-reader-pro-preview-debug-v<ver>-<code>-<sha>` | Pro preview debug APK | CI, `test` job | every PR / push |
| `localmd-reader-free-play-aab-v<ver>-<code>-<sha>` | Free signed release AAB | Play Release, `build-release-aab` job | manual dispatch, `channel: free-play` |
| `localmd-reader-pro-preview-artifact-aab-v<ver>-<code>-<sha>` | Pro preview signed release AAB | Play Release, `build-release-aab` job | manual dispatch, `channel: pro-preview-artifact` |

`<ver>` = version name, `<code>` = version code, `<sha>` = 7-character commit SHA
(resolved by the "Resolve build metadata" step from `scripts/version-env.sh`).

- **Name stability**: the scheme is fixed; a version bump changes only the
  embedded `<ver>`/`<code>`, so every build is uniquely and predictably named and
  names never collide across versions.
- **Credentials**: the CI debug APKs need no Play Console credentials. The release
  AAB job builds a signed AAB artifact without uploading to Play unless
  `upload_to_play` is requested (and only `free-play` may upload).

Where to download:

- For a pull request: open its CI check, go to the CI run, then **Artifacts**.
- For a release AAB: Actions tab → Play Release → the run → **Artifacts**.

## Device Smoke

**Shared setup (all emulator workflows):** the six emulator-driven workflows
(`device-smoke`, `gesture-smoke`, `smoke-render-l5`, `theme-screenshots`,
`visual-regression-check-limited`, `exploration-emulator`) share one composite
action, `.github/actions/emulator-build-setup`, for the identical setup —
resolve build metadata, set up Java/Gradle, enable KVM, and build the debug APK
for the given `flavor` (Free or ProPreview). The build step must invoke the
repository Gradle wrapper (`./gradlew`), not a runner-provided `gradle`, so AGP
compatibility follows the checked-in wrapper version. Each workflow keeps only
its own emulator-runner step, upload, and summary. A setup fix now lands in one
place instead of six near-identical copies (the duplication that left
device-smoke and smoke-render-l5 silently broken until 2026-06-12).

**Emulator-runner script constraint (all smoke workflows):** the
`reactivecircus/android-emulator-runner` action executes each `script:` line
in a SEPARATE `sh -c`, so shell variables and function definitions do NOT
persist across lines (proven on gesture-smoke run 27285682358; device-smoke
and smoke-render-l5 were broken by this until 2026-06-12). Every script line
must be self-contained — inline the artifact directory instead of assigning
it to a variable, and fold helper logic into a single statement.

`.github/workflows/device-smoke.yml` is manual-only by `workflow_dispatch`. It
builds the Free debug APK, boots an Android 35 emulator, installs the APK, and
runs `scripts/emulator-smoke.sh`, which walks a smoke ladder and fails on the
first problem:

- **L2 launch** – start `MainActivity` and confirm the process is alive.
- **L3 single-file intent** – open one Markdown document via the `OPEN_TEXTS`
  intent (the same content-as-base64 intent `scripts/mdlite-open.sh` uses) and
  confirm no crash.
- **L4 multiple-file intent** – open two Markdown documents as tabs and confirm
  no crash.

The fixtures live in `scripts/smoke-fixtures/`. The workflow stays manual until
its emulator runtime is proven stable enough for pull-request CI.

`.github/workflows/smoke-render-l5.yml` is a separate manual-only workflow
(`workflow_dispatch`) for **L5 render-assert** smoke. It opens
`scripts/smoke-fixtures/smoke-render.md` via the `OPEN_TEXTS` intent and asserts
that table, code-block, and Mermaid visible text is present in the UIAutomator
UI dump via `scripts/smoke-render-assert-l5.sh`. This workflow is
**flaky-tolerant and is NOT a required PR check**; it must not be added to
branch protection rules. Its purpose is to detect rendering regressions that
crash-free smoke (L2–L4) cannot catch.

On every run (pass or fail) it captures `smoke-artifacts/logcat.txt` and a
screenshot, uploads them as a `device-smoke-evidence-…` artifact, and writes a
run summary (package, channel, version, commit, result) to the workflow summary.
This makes a failure easy to classify as install, launch, intent open, render,
or crash without local reproduction. Device smoke opens only repository fixtures,
and the app holds no INTERNET permission and does not log document contents, so
the logcat is designed not to include user documents or secrets.

## Harness ROI

`.github/workflows/harness-roi.yml` runs weekly and by manual dispatch as an
advisory measurement job. It collects three signals into the workflow summary and
the `harness-roi-report` artifact:

- logical gate history from `scripts/collect-gate-history.sh`
- test pyramid balance from `scripts/test-balance-report.sh`
- complexity and duplication hot spots from `scripts/measure-complexity.sh`

The job is deliberately non-blocking. It does not remove tests, downgrade gates,
or change branch protection automatically. Its output is evidence for the
harness ROI framework: keep, strengthen, consolidate, downgrade to advisory, or
propose removal with human approval.

## Branch Protection

`main` is protected. Do not push directly to `main`; create a pull request from a
feature branch.

The following checks must pass before merging to `main`:

- `fitness` (fast source-level guards: per-file line count, hard constraints)
- `test`
- `gradle-build`
- `mutation`

A pull request is required before merging (with zero required approvals, so solo
development is not blocked on self-approval, but direct pushes to `main` are
refused). The pull request branch must be up to date with the base branch. Force
pushes and branch deletion are disabled. Conversations must be resolved before
merge. Protection is enforced for admins as well.

The protection policy is reproducible. Re-apply or audit it with:

```sh
sh scripts/setup-branch-protection.sh Yos-K/localmd-reader main
```

Start work with:

```sh
sh scripts/start-work.sh feature/short-topic
```

After implementation, run local checks and open a pull request with:

```sh
sh scripts/open-pr.sh "type: short title"
```

Commit messages and pull request titles follow Conventional Commits. On a pull
request the `fitness` job validates the title with
`scripts/check-conventional-title.sh` (the same check `scripts/open-pr.sh` runs
locally), so a non-conforming title fails the required checks.

## Play Release

`.github/workflows/play-release.yml` is manual-only by `workflow_dispatch`.

Before building, it runs one preflight command that prints a concise pass/fail
summary and fails loudly on any problem:

```sh
sh scripts/release-preflight.sh
```

The preflight aggregates the source-level release checks: version consistency,
release notes present and within Play Store's 500-character limit
(`play-store/release-notes/en-US/whatsnew.txt` and `ja-JP/whatsnew.txt` checked
by `scripts/check-release-notes.sh` using sh+awk — no python3 dependency, Termux
compatible; docs/release/*.md and *.ja.md presence also verified),
hard constraints (no INTERNET, reader WebView JavaScript off), no committed
secrets, third-party notices, the free upload package id
(`io.github.yosk.mdlite`, verified identical in the manifest and
`app/build.gradle`), the Gradle flavor suffixes (`.pro` / `.debug`), and the Play
upload free-only guard. The default script release path builds
`io.github.yosk.mdlite` for every channel and pro preview is never uploaded, so
the only package that reaches Play is the free one. APK-level checks still run
later against the built APK.

It then builds a signed release AAB with the existing release script:

```sh
sh scripts/build-release-aab.sh
```

Choose one channel:

- `free-play`: signed release AAB for the Play package.
- `pro-preview-artifact`: signed AAB with Pro features enabled for verification.

Only `free-play` can be uploaded to Google Play. `pro-preview-artifact` is
guarded so it fails before upload if `upload_to_play` is accidentally enabled.

By default, the workflow only builds and stores the AAB artifact. Set
`upload_to_play` to `true` to upload the `free-play` AAB to Google Play.

The AAB artifact is named
`localmd-reader-<channel>-aab-v<versionName>-<versionCode>-<shortSha>` and can be
downloaded from the workflow run's **Artifacts** section.

Each run writes a summary (package, channel, build system, version, commit,
track, and whether the Play upload was executed or skipped) to the workflow
summary, so a reviewer can confirm what shipped without reading the logs.

The default track is `alpha`, matching the current closed testing track.

`changes_not_sent_for_review` defaults to `false` because Play Console now sends
changes for review automatically for this app. Setting it to `true` can make the
Android Publisher API reject the commit.

The workflow intentionally supports only `completed` and `draft` release
statuses. Staged rollout with `inProgress` should be added as a separate
workflow when rollout percentage and halt/resume rules are defined.

## Standard Play Upload Checks

Source CI runs in the public `Yos-K/localmd-reader` repository. Signing and
Play uploads run only in the private `Yos-K/localmd-reader-release` repository.
The local remotes are `origin` (public source) and `release` (private release).

Use the wrapper script instead of dispatching the workflow by hand when
uploading the Free app to Play Console.

```sh
sh scripts/play-upload-free-github-actions.sh Yos-K/localmd-reader-release main alpha
```

The script dispatches the Play Release workflow with `free-play`, `alpha`,
`upload_to_play=true`, and `changes_not_sent_for_review=false`, then prints the
run id and follow-up commands.

Watch the run with:

```sh
gh run watch RUN_ID --repo Yos-K/localmd-reader-release --interval 20
```

When a run fails, summarize the run, failed job, and failed-job logs with:

```sh
sh scripts/github-run-report.sh RUN_ID Yos-K/localmd-reader-release
```

If Play upload preflight fails, the `play-console` Environment is missing
Variables or Secrets. The log prints only missing names, never secret values.

## GitHub Environments

Create two environments:

- `release-build`
- `play-console`

They can be created through the GitHub API:

```sh
sh scripts/setup-github-actions-repo.sh Yos-K/localmd-reader-release
```

Put release keystore secrets in both environments if you want both artifact-only
release builds and Play uploads to use protected secrets.

Put Google Cloud OIDC variables only in `play-console`.

For `play-console`, enable required reviewers if the repository plan supports
environment reviewers. GitHub only exposes environment secrets to the job after
the environment gate is passed.

Environment reviewers are intentionally not configured by the script. A solo
developer can accidentally block their own release flow when required reviewers
are configured too aggressively.

## Required GitHub Secrets And Variables

When `upload_to_play` is requested, `scripts/play-upload-preflight.sh` runs early
in the release job and fails fast if any of the values below is unset — without
printing any value — naming each missing variable/secret. This avoids building a
signed AAB only to fail later at authentication or upload.

- `MDLITE_RELEASE_KEYSTORE_BASE64`: base64 encoded release keystore.
- `MDLITE_RELEASE_KEY_ALIAS`: release key alias.
- `MDLITE_RELEASE_STORE_PASS`: release keystore password.
- `MDLITE_RELEASE_KEY_PASS`: release key password.
- `GCP_WORKLOAD_IDENTITY_PROVIDER`: Workload Identity Provider resource name.
- `GCP_SERVICE_ACCOUNT`: service account email for Play releases.

Register secrets with GitHub CLI:

```sh
gh secret set MDLITE_RELEASE_KEYSTORE_BASE64 --env release-build --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_ALIAS --env release-build --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_STORE_PASS --env release-build --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_PASS --env release-build --repo Yos-K/localmd-reader-release

gh secret set MDLITE_RELEASE_KEYSTORE_BASE64 --env play-console --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_ALIAS --env play-console --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_STORE_PASS --env play-console --repo Yos-K/localmd-reader-release
gh secret set MDLITE_RELEASE_KEY_PASS --env play-console --repo Yos-K/localmd-reader-release
gh variable set GCP_WORKLOAD_IDENTITY_PROVIDER --env play-console --repo Yos-K/localmd-reader-release
gh variable set GCP_SERVICE_ACCOUNT --env play-console --repo Yos-K/localmd-reader-release
```

## Create The Keystore Secret

Run this locally and register the output as `MDLITE_RELEASE_KEYSTORE_BASE64`.

```sh
base64 -w 0 /path/to/mdlite-reader-release.jks
```

On systems without `-w`, use:

```sh
base64 /path/to/mdlite-reader-release.jks | tr -d '\n'
```

Do not commit keystores, service account JSON, or Play Console tokens. The
preferred CI authentication path is GitHub OIDC plus Google Cloud Workload
Identity Federation, so no service account JSON key is needed.

As a backstop, the `fitness` job runs `scripts/check-no-committed-secrets.sh`,
which fails the build if a keystore/key/service-account file or PEM private key
is ever tracked. Release workflows pass secrets only through environment
variables and never echo their values.
