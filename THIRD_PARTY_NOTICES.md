# Third-Party Notices

This project includes third-party software only when the license allows
redistribution in this repository and in the Android app package.

## Mermaid

- Component: Mermaid
- Version: 11.15.0
- Source: https://github.com/mermaid-js/mermaid
- Package: https://www.npmjs.com/package/mermaid
- License: MIT
- Included file: `src/main/assets/mermaid.min.js`
- Use in LocalMD Reader: Pro-only local Mermaid diagram rendering.

The included `mermaid.min.js` file is copied from the npm package
`mermaid@11.15.0` without source modification.

The full MIT license text is preserved in
`docs/third-party/mermaid-11.15.0-MIT.txt`.

The distributed Mermaid bundle may contain notices for transitive open-source
components. Those notices are preserved inside `src/main/assets/mermaid.min.js`
and must not be removed when updating the file.

## Google Play Billing Library

- Component: Google Play Billing Library
- Version: 9.0.0
- Source: https://developer.android.com/google/play/billing
- Package: `com.android.billingclient:billing:9.0.0`
- Repository: Google Maven
- License: Android Software Development Kit License
- Included file: downloaded during builds by `scripts/prepare-android-dependencies.sh`
- Use in LocalMD Reader: Pro purchase flow integration.

The Billing Library binary is not committed to this repository. CI and local
release builds download it from Google Maven and include it in the Android
artifact when the build scripts are run after dependency preparation.

## Policy

- Do not add generated images, icons, fonts, JavaScript libraries, or other
  third-party assets without adding their license information here.
- Do not use assets copied from Play Store listings, app icons, screenshots,
  websites, or social posts unless their license explicitly allows reuse.
- Prefer source-controlled text or vector assets created specifically for this
  project.
- For npm packages, record the package name, exact version, package URL,
  upstream URL, license, and included files.
- Preserve required copyright and license notices for every bundled dependency.
