# Veyra

Android overlay companion with a minimal black UI, update checks via GitHub Releases, and a download website.

**Important:** The overlay does not magically enhance video pixels from other apps. Android screen capture requires user consent and may be blocked by DRM/protected surfaces. Version 0.1 focuses on the floating UI, controls, and updates; actual cross-app video enhancement is **not implemented**.

## Build
GitHub Actions builds the debug APK with Java 17 / Gradle 8.9. The APK is available in workflow artifacts. Publishing a release with an APK attached enables the update prompt.

## Site
The `docs/` directory is ready for GitHub Pages. Enable Pages in repository Settings > Pages > Deploy from branch > main > /docs.

## Releases
Create a release tagged `v0.1.1` or later and attach the compiled APK named `veyra.apk`. The app checks the latest public release when opened or when pressing Check updates.

## Permissions
Overlay permission is requested explicitly. No accessibility service, hidden recording, or protected content bypass.
