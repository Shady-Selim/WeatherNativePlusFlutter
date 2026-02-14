# Weather Native Plus Flutter (Android)

This is a native Android application built with **Jetpack Compose** that demonstrates how to embed a **Flutter module** directly into a native view hierarchy.

## Features
- **Jetpack Compose UI**: A native welcome screen built entirely with Compose.
- **Embedded Flutter**: The weather screen is a Flutter Dart widget hosted inside a `FlutterFragment`.
- **Bidirectional Navigation**:
  - **Native to Flutter**: Triggered by a Compose Button.
  - **Flutter to Native**: Triggered by a Flutter back button using `MethodChannel`.
- **Pre-warmed Engine**: Uses `FlutterEngineCache` to ensure the Flutter UI loads instantly without a "white flash".

## Project Structure
- `MainActivity.kt`: Manages the application state, initializes the `FlutterEngine`, and sets up the `MethodChannel` for back navigation.
- `AndroidView`: Used to host the `FragmentContainerView` which in turn displays the `FlutterFragment`.

## Requirements
- Android Studio Iguana or newer.
- Flutter SDK installed and configured.
- The [`flutter_weather_module`](https://github.com/Shady-Selim/flutter_weather_module) must be located in the directory adjacent to this project (as configured in `settings.gradle`).

## Setup
1. Ensure the `flutter_weather_module` is in the same parent folder as this project.
2. Open this project in Android Studio.
3. Sync Project with Gradle Files.
4. Run the `app` module on an emulator or physical device.

## Navigation Logic
The app uses a `currentScreen` state in `MainActivity`. 
- When `currentScreen == "WELCOME"`, the native Compose UI is shown.
- When `currentScreen == "FLUTTER"`, the `AndroidView` hosting Flutter is shown.
- The Flutter side calls `invokeMethod('goBack')` which the native side listens to to switch the state back to `"WELCOME"`.
