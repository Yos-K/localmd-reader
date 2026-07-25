# Project-local Android SDK versions.
#
# Termux aapt2 cannot load API 35+ platform resources. Gradle CI remains the
# release authority and compiles against API 36; local scripts use API 33 only
# as a compatible compile platform while the manifest still targets API 36.
export ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"
export ANDROID_BUILD_TOOLS="${ANDROID_BUILD_TOOLS:-35.0.2}"

# Termux build-tools 35.0.2 links d8 to the obsolete system D8 3.3.20,
# which crashes while dexing AndroidX Core 1.15.0. Keep the Termux-compatible
# aapt2/zipalign tools, but use the complete SDK D8 8.6.2 from 35.0.0.
export D8="${D8:-${ANDROID_HOME:-$HOME/AndroidDev/sdk}/build-tools/35.0.0/d8}"
