# Project-local Android SDK versions.
#
# Termux aapt2 cannot load API 35+ platform resources. Gradle CI remains the
# release authority and compiles against API 36; local scripts use API 33 only
# as a compatible compile platform while the manifest still targets API 36.
export ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"
export ANDROID_BUILD_TOOLS="${ANDROID_BUILD_TOOLS:-35.0.2}"
