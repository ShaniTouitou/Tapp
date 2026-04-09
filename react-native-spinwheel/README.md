# react-native-spinwheel

A Kotlin Android home screen widget + React Native wrapper that renders an interactive Spin Wheel on the device home screen.

---

## What it does

- Fetches a remote JSON configuration (config URL is configurable)
- Downloads image assets from Google Drive public URLs
- Caches config and assets locally for offline use
- Renders a spin wheel on the Android home screen using `AppWidgetProvider` + `RemoteViews`
- Animates the wheel smoothly when the user taps it (Handler-based loop, works in background)
- Persists wheel state (rotation, result, asset paths, last fetch time) in `SharedPreferences`
- Exposed as a React Native library installable via `.tgz`

---

## Installation

### From the local folder (development)

```bash
npm install ../react-native-spinwheel
```

### As a packaged release

```bash
cd react-native-spinwheel
npm pack                          # produces react-native-spinwheel-0.0.1.tgz
cd ../YourApp
npm install ../react-native-spinwheel/react-native-spinwheel-0.0.1.tgz
```

---

## Usage (React Native in-app component)

```tsx
import SpinWheel from 'react-native-spinwheel';

<SpinWheel
  configUrl="https://your-config-url.json"
  style={{ width: 300, height: 300 }}
/>
```

The home screen widget is registered automatically when the app is installed — no extra code needed.

---

## JSON Configuration Format

```json
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
  "meta": { "version": 1, "copyright": "Tapp" }
}
```

- `network.assets.host` — base URL prepended to all asset IDs
- `wheel.assets.*` — Google Drive file IDs (joined to host)
- `wheel.rotation.*` — animation parameters (duration ms, min/max full spins)

---

## Project Structure

```
react-native-spinwheel/
├── src/
│   ├── index.ts                        # React Native JS export
│   └── SpinWheelNativeComponent.tsx    # Native component bridge
├── android/
│   └── src/main/
│       ├── AndroidManifest.xml         # Registers AppWidgetProvider receiver
│       ├── java/com/shani/spinwheel/
│       │   ├── SpinWheelPackage.kt          # RN package registration
│       │   ├── SpinWheelView.kt             # In-app native view
│       │   ├── SpinWheelViewManager.kt      # RN ViewManager
│       │   ├── data/
│       │   │   ├── cache/
│       │   │   │   ├── AssetFileCache.kt    # Caches downloaded images to disk
│       │   │   │   ├── ConfigCache.kt       # Caches raw JSON config to disk
│       │   │   │   └── WidgetPreferences.kt # SharedPreferences: last fetch time
│       │   │   ├── model/WidgetConfig.kt    # JSON data model (kotlinx-serialization)
│       │   │   ├── remote/
│       │   │   │   ├── AssetDownloader.kt   # Downloads assets via OkHttp
│       │   │   │   └── ConfigApi.kt         # Fetches JSON config via OkHttp
│       │   │   └── repository/
│       │   │       ├── AssetRepository.kt   # Cache-first asset loading
│       │   │       └── SpinWheelRepository.kt # Config fetch + cache fallback
│       │   └── widget/
│       │       ├── SpinWheelWidgetReceiver.kt  # AppWidgetProvider: lifecycle + animation
│       │       ├── SpinWheelWidgetUpdater.kt   # Orchestrates remote refresh
│       │       └── WidgetState.kt              # SharedPreferences: widget state
│       └── res/
│           ├── drawable/widget_loading_bg.xml  # Gradient for loading overlay
│           ├── layout/spin_wheel_widget_main.xml
│           └── xml/spin_wheel_widget_info.xml  # Widget metadata
└── package.json
```

---

## Android Implementation Details

### Libraries used

| Library | Purpose |
|---|---|
| `com.squareup.okhttp3:okhttp` | Network requests (config + assets) |
| `kotlinx-serialization-json` | JSON parsing |
| `kotlinx-serialization-cbor` | Binary config encoding |
| `kotlinx-coroutines-android` | Background work |
| Jetpack Compose | In-app spin wheel UI |

### Home screen widget

The widget is implemented as an `AppWidgetProvider` (not Glance):

- **Layout**: `spin_wheel_widget_main.xml` — `FrameLayout` with `ImageView`s for background, wheel, frame, and spin button, plus a styled loading overlay
- **Animation**: `Handler.postDelayed` loop at ~16 ms intervals, calculating rotation from `AccelerateDecelerateInterpolator` and wall-clock elapsed time. Rotation is pushed via `RemoteViews.setFloat("setRotation")` + `partiallyUpdateAppWidget` — hardware-accelerated on the launcher side
- **Why Handler, not ValueAnimator**: `ValueAnimator` depends on Choreographer VSYNC signals, which are not delivered to background processes with no visible window. The `Handler` message queue runs off the system clock and works reliably in the background
- **Spin state**: `AtomicBoolean` in the companion object — auto-resets on every process start; never stuck in `SharedPreferences`
- **Process lifetime**: `goAsync()` in `onReceive` holds a wakelock for the animation duration (≈ 2 s)
- **Loading state**: While assets download, the widget shows a purple-themed overlay ("SPIN WHEEL / ✨ Your lucky moment is almost here! ✨") instead of a blank transparent rectangle

### SharedPreferences keys

| File | Prefs name | Data stored |
|---|---|---|
| `WidgetPreferences` | `spinwheel_prefs` | Last successful config fetch timestamp |
| `WidgetState` | `spinwheel_widget_state` | Config URL, asset paths, current rotation, result, animation params |

---

## Demo App

See `DemoSpinWheelApp/` for a full working React Native app that uses this library and has the home screen widget pre-configured.

```bash
cd DemoSpinWheelApp
npm install
npx react-native run-android --port 8082
```

After installing: long-press the home screen → Widgets → **Spin Wheel**.

---

## Notes

- Android only (iOS not implemented)
- Minimum SDK: 24
- On first launch, the widget needs a few seconds to download assets. Some OEM devices (Xiaomi MIUI, Huawei EMUI) may require opening the app once before background network access is granted
- The widget config URL can be changed programmatically via `SpinWheelWidgetUpdater.saveConfigUrl(context, url)`
