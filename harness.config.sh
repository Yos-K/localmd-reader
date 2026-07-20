# harness.config.sh — LocalMD Reader's project-specific values for harness-kit scripts.
#
# harness-kit adapter scripts source this file (if present) from the repo root after
# resolving the root, so project-specific values stay here while the kit scripts stay
# generic and syncable. Plain shell (sourced with `.`): set environment variables only.
# Never put secrets here.
#
# Consumed kit scripts (harness_kit_version is pinned in sync-manifest.yaml):
#   - scripts/check-file-sizes.sh    (kit adapters/android-jvm/scripts/check-file-sizes.sh)
#   - scripts/run-mutation-tests.sh  (kit adapters/android-jvm/scripts/run-mutation-tests.sh)

# --- check-file-sizes.sh ---
# 300-line per-file limit (matches the project's file-size rule and .githooks/pre-commit).
FITNESS_MAX_LINES=300
# Exceptions list default ($ROOT/scripts/fitness-exceptions.txt) is correct; left unset.

# --- run-mutation-tests.sh ---
BUILDCONFIG_PACKAGE=io.github.yosk.mdlite.infrastructure
TARGET_CLASSES="io.github.yosk.mdlite.domain.*,io.github.yosk.mdlite.viewer.*,io.github.yosk.mdlite.file.*,io.github.yosk.mdlite.infrastructure.*"
TARGET_TESTS="io.github.yosk.mdlite.*"
# ViewerText* is the i18n string table (~180 NO_COVERAGE string returns); excluded so the
# score reflects real logic (see docs/harness/mutation-analysis-rule.md).
EXCLUDED_CLASSES="*Test,*Tests,*Property,*Properties,io.github.yosk.mdlite.viewer.ViewerText*"
# architecture.* asserts structure (cannot kill mutants); *Property PBT explodes PITest runtime.
EXCLUDED_TESTS="io.github.yosk.mdlite.architecture.*,*Property,*Properties"
MUTATION_THRESHOLD=82  # ratchet floor — never lower (see mutation-analysis-rule.md)
# Logic injection: the Android-free code references both flags, so the BuildConfig stub must
# expose both; and MermaidRenderErrorHtml (in presentation/) is part of the Android-free set.
BUILDCONFIG_FIELDS="    public static final boolean PRO_FEATURES_ENABLED = false;
    public static final boolean PLAY_BILLING_ENABLED = false;"
EXTRA_MAIN_SOURCES="src/main/java/io/github/yosk/mdlite/presentation/MermaidRenderErrorHtml.java"

# More variables are added here as additional kit scripts adopt the consumable form
# (emulator-smoke.sh APP_*, capture-theme-screenshots.sh APP_THEMES, etc.).

# 二階ループ（生存証明プローブ）: localmd の予防ゲートが「黙って壊れていない」ことを probe する。
# title/secrets は kit の core と byte 一致なので probe の合成違反注入がそのまま効く。
# （check-file-sizes は src/**.java を走査するため probe の filesize 注入(*.sh)とは別物→対象外）
PROBE_GATES="scripts/check-conventional-title.sh:title scripts/check-no-committed-secrets.sh:secrets"

# 仕様駆動学習ループ（harness-spec-test-loop）のモード。localmd はバイブコーディングで「別個の人間の
# 意図」が無いため autonomous: エージェントが「機能としてどうあるべきか」から判断して規則を canon 化し
# ACTIVE テストを書く。ただしビジネスポリシー型の判断（tier/失効/猶予期間 等）は明示フラグを残し、
# 金銭/契約/UX を驚かせうる判断はその項目だけ expert-gated に自動 escalate される（domain-modeler 規則）。
SPEC_LOOP_MODE=autonomous
