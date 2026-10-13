# SJP Backend Tracker (Android)

An Android WebView wrapper for the official Sindh Job Portal. It opens SJP automatically and injects the bundled tracker JavaScript when the logged-in page appears to be the Applied Posts / Applied Jobs page. The tracker code is bundled in `app/src/main/assets/tracker.js`; candidates do not paste a bookmarklet.

## Build an APK with GitHub Actions
1. Create a new GitHub repository (recommended name: `sjp-backend-tracker-android`).
2. Upload the contents of this project, preserving folders.
3. Open **Actions** and run **Build SJP Backend Tracker APK** (or push to `main`).
4. Open the completed workflow run and download the artifact named `SJP-Backend-Tracker-APK`.
5. Extract `app-debug.apk` and install it on Android. Android may ask permission to install apps from that source.

## Important limitations
- This is a first build of the source project, not a pre-tested APK. SJP may change its page text, login flow, API behavior, or security policy.
- The tracker uses the same page-context JavaScript approach as the bookmarklet. API access still depends on SJP cookies and browser security headers.
- Status-change checks occur when the tracker runs; Android does not continuously poll in the background.
- Use only the official SJP site and do not share your login credentials.
- Android 7.0+ (API 24+) is the intended minimum.
