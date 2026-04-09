DemoSpinWheelApp

This is the demo React Native application for the Tapp Spin Wheel library.
It is used to demonstrate the integration of the native Android Spin Wheel widget inside a React Native project 
and to verify that the component works correctly in a real app environment.

Overview:

The demo app showcases the following functionality:

Loading remote JSON configuration
Loading image assets from Google Drive public URLs
Rendering the wheel UI from remote assets
Tap-to-spin interaction
Local caching for config and assets
Offline fallback behavior
SharedPreferences persistence for the last successful fetch time

Project Relationship:

DemoSpinWheelApp: demo React Native application
react-native-spinwheel: reusable React Native wrapper and native Android library
react-native-spinwheel-0.0.1.tgz: packaged installable version of the library

Requirements:

Node.js
npm
Android Studio
Android SDK
JDK
Android emulator or physical Android device

Setup:

Step 1:
Install the demo app dependencies:

npm install

Step 2:
Install the local spin wheel library:

npm install ../react-native-spinwheel

Running the Demo:

Step 1:
Start Metro:

npx react-native start --reset-cache

Step 2:
In a second terminal, run the Android app:

npx react-native run-android

What the Demo Does:

Step 1:
Fetches the remote widget configuration JSON.

Step 2:
Builds full asset URLs using the Google Drive host and asset file IDs from the config.

Step 3:
Downloads and displays the wheel assets.

Step 4:
Renders the spin wheel UI.

Step 5:
Allows the user to tap and spin the wheel.

Step 6:
Uses cached configuration and assets when network is unavailable.

Configuration Source:

The demo uses a remote JSON configuration file defined in MainActivity.kt.

The configuration controls:

asset host URL
asset file IDs
spin duration
minimum spins
maximum spins
cache-related behavior

Example Demo Flow:

Launch the app
Fetch remote config
Download wheel assets
Display the wheel
Tap to spin
Reopen without internet and use cached data

Troubleshooting:

If Metro is already running on port 8081, stop the old process and restart it.

If native Kotlin code changes, rebuild the Android app.

If only the remote JSON changes, no rebuild is needed. In some cases, clearing app data may help:

adb shell pm clear com.demospinwheelapp

Expected Result:

When the app launches successfully,
it should display the Spin Wheel UI with remote assets
and allow the user to spin the wheel by tapping it.

Verification Summary:

The following behavior was verified:

The demo app runs successfully on Android
The wheel spins when tapped
Remote JSON changes affect the UI
Assets are loaded from Google Drive public URLs
Cached data is used when network is unavailable
SharedPreferences stores the last successful fetch time

Notes:

The demo app was tested on Android emulator
Asset loading was verified using Google Drive public file IDs
Offline cache fallback was verified
SharedPreferences persistence was verified on device