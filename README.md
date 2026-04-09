Tapp Spin Wheel

Tapp Spin Wheel is a minimal Android Spin Wheel widget library built with Kotlin and wrapped for React Native. The project includes a reusable native Android component, a React Native wrapper, and a working demo React Native application.


Overview

This project includes:

- A native Android Spin Wheel widget implemented in Kotlin
- A React Native wrapper for integrating the widget into React Native applications
- A demo React Native app for testing and showcasing the component
- Support for remote JSON configuration
- Support for loading image assets from Google Drive hosted public URLs
- Local caching for configuration and assets
- SharedPreferences persistence for storing the last successful fetch time
- A packaged .tgz version of the library for reuse


Features

- Fetches remote JSON configuration
- Loads wheel assets from Google Drive public URLs
- Renders the wheel UI using the provided assets
- Spins the wheel when tapped
- Caches configuration and assets locally
- Falls back to cache when network is unavailable
- Stores the last successful fetch time using SharedPreferences
- Exposes the widget as a reusable React Native component
- Includes a working demo app


Project Structure

Tapp/
react-native-spinwheel/               Native Android library + React Native wrapper
android/src/main/java/
com/shani/spinwheel/
data/
cache/                        ConfigCache, AssetFileCache, WidgetPreferences
model/                        WidgetConfig data models
remote/                       ConfigApi, AssetDownloader
repository/                   SpinWheelRepository, AssetRepository
presentation/ui/                SpinWheelRemoteScreen, rememberCachedImageFile
SpinWheelView.kt
SpinWheelViewManager.kt
SpinWheelPackage.kt
src/
index.ts                          JS entry point
SpinWheelNativeComponent.tsx
package.json
DemoSpinWheelApp/                     Demo React Native application
react-native-spinwheel-0.0.1.tgz      Packaged installable library


Requirements

- Node.js >= 22
- npm
- Android Studio
- Android SDK
- JDK
- Android emulator or Android device


Installation

Step 1 - Clone the project

Clone or copy the project to your machine and open it in your preferred editor.

Step 2 - Configure Android SDK path

Before running the demo app, make sure your Android SDK path is configured.

You can do this in one of the following ways:

Option A - Recommended: create a local properties file

Create a local file at:

DemoSpinWheelApp/android/local.properties

Add your local Android SDK path, for example:

sdk.dir=C:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk

Option B - Set the ANDROID_HOME environment variable

Instead of creating local.properties, you can configure the SDK path through an environment variable.

Example for Windows PowerShell:

$env:ANDROID_HOME="C:\Users\YOUR_USER\AppData\Local\Android\Sdk"

You can find the correct SDK path in Android Studio under:

File > Settings > Android SDK

Note: local.properties is machine-specific and should not be committed to the repository.

Step 3 - Install demo app dependencies

Open a terminal inside the DemoSpinWheelApp folder and run:

    cd DemoSpinWheelApp
    npm install

Step 4 - Install the library

Option A - from local source:

    npm install ../react-native-spinwheel

Option B - from .tgz:

    npm install ../react-native-spinwheel-0.0.1.tgz


Running the Demo App

Option A - Development mode (requires Metro)

Terminal 1 - Start Metro bundler:

    cd DemoSpinWheelApp
    npx react-native start --reset-cache

Terminal 2 - Run on Android:

    cd DemoSpinWheelApp
    npx react-native run-android

Option B - Offline mode (no Metro needed)

    cd DemoSpinWheelApp

    npx react-native bundle --platform android --dev false --entry-file index.js --bundle-output android/app/src/main/assets/index.android.bundle --assets-dest android/app/src/main/res/

    cd android
    .\gradlew assembleDebug
    adb install -r app\build\outputs\apk\debug\app-debug.apk
    adb shell am start -n com.demospinwheelapp/.MainActivity


Usage

Example usage inside a React Native application:

    import React from 'react';
    import { SafeAreaView, StyleSheet, Text, View } from 'react-native';
    import SpinWheel from 'react-native-spinwheel';

    export default function App() {
      return (
        <SafeAreaView style={styles.container}>
          <Text style={styles.title}>Demo Spin Wheel</Text>
          <View style={styles.wrapper}>
            <SpinWheel
              configUrl="https://your-config-endpoint.com/config.json"
              style={{ width: 300, height: 300 }}
            />
          </View>
        </SafeAreaView>
      );
    }

    const styles = StyleSheet.create({
      container: { flex: 1, backgroundColor: '#fff', justifyContent: 'center', alignItems: 'center' },
      title: { fontSize: 24, fontWeight: '700', marginBottom: 24 },
      wrapper: { justifyContent: 'center', alignItems: 'center' },
    });


Remote Configuration

The widget reads its configuration from a remote JSON URL passed via the configUrl prop.

JSON Schema example:

    {
      "data": [
        {
          "id": "wheel_minimal",
          "name": "Minimal Wheel Widget Configuration",
          "type": "Widget",
          "network": {
            "attributes": {
              "refreshInterval": 300,
              "networkTimeout": 30000,
              "retryAttempts": 3,
              "cacheExpiration": 3600,
              "debugMode": false
            },
            "assets": {
              "host": "https://drive.google.com/uc?export=download&id="
            }
          },
          "wheel": {
            "rotation": {
              "duration": 2000,
              "minimumSpins": 3,
              "maximumSpins": 5,
              "spinEasing": "easeInOutCubic"
            },
            "assets": {
              "bg": "GOOGLE_DRIVE_FILE_ID",
              "wheelFrame": "GOOGLE_DRIVE_FILE_ID",
              "wheelSpin": "GOOGLE_DRIVE_FILE_ID",
              "wheel": "GOOGLE_DRIVE_FILE_ID"
            }
          }
        }
      ],
      "meta": {
        "version": 1,
        "copyright": "Tapp"
      }
    }

Asset URLs are constructed as host + assetId, for example:
https://drive.google.com/uc?export=download&id=GOOGLE_DRIVE_FILE_ID

Caching

The project supports local caching for:

- Remote JSON configuration (widget_config.json in app cache dir)
- Downloaded image assets (spinwheel_assets/ in app cache dir)

If the device is offline, the widget automatically falls back to previously cached data.


SharedPreferences

The last successful config fetch time is stored using SharedPreferences.

File:   spinwheel_prefs.xml
Key:    last_fetch_time
Value:  Unix timestamp in milliseconds

To verify on device:

    adb shell "run-as com.demospinwheelapp cat /data/data/com.demospinwheelapp/shared_prefs/spinwheel_prefs.xml"


Packaging the Library

To create the reusable .tgz package run:

    cd react-native-spinwheel
    npm pack

This generates: react-native-spinwheel-0.0.1.tgz


Android Dependencies

okhttp3                          Network requests
kotlinx-serialization-json       JSON parsing
kotlinx-serialization-cbor       Binary serialization
androidx.compose                 Wheel UI rendering
SharedPreferences                State persistence


Testing Summary

The following functionality was verified:

- Demo app runs successfully on Android emulator
- Wheel spins when tapped
- Remote JSON changes affect the UI (verified by updating Gist)
- Assets are loaded from Google Drive public URLs
- Cached data is used when network is unavailable
- SharedPreferences stores the last successful fetch time (verified via adb shell)


Deliverables

Native Android widget library (Kotlin)    react-native-spinwheel/android/
React Native wrapper                      react-native-spinwheel/src/
Demo React Native app                     DemoSpinWheelApp/
Packaged .tgz library                     react-native-spinwheel-0.0.1.tgz
Full source code                          This repository
Documentation                             This README


Notes

- The demo app was tested on Android emulator (API 35 - Baklava)
- Asset loading was verified using Google Drive public file IDs
- Cache fallback was verified by reopening the app without network access
- SharedPreferences persistence was verified on device via ADB
