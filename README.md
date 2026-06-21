# Browser for TV

A minimalist, high-performance web browser designed specifically for Android TV.

## Features
- **Minimum Android 8 Support**: Compatible with a wide range of Android TV devices (API Level 26+).
- **D-Pad Navigation**: Fully navigable using a standard TV remote control (D-pad spatial navigation).
- **Minimalist Design**: Clean interface focusing on content with an auto-hiding navigation bar.
- **Low RAM Usage**: Built on the native system WebView to minimize memory footprint.
- **Fluent Process**: Hardware-accelerated and optimized for smooth scrolling and fast page loads.

## Development Setup
1. Clone the repository and open the project in Android Studio.
2. The project uses **Jetpack Compose** for the UI overlay and `AndroidView` for the core `WebView`.
3. To build and run from the command line:
   ```bash
   ./gradlew assembleDebug
   ```
4. Deploy the generated APK (`app/build/outputs/apk/debug/app-debug.apk`) to an Android TV emulator (API 26+) or a physical device.
