# Walton BLDC Fan IR Remote

An Android app that turns a phone's built-in IR blaster into a remote for the **Walton Super Saver-IR BLDC ceiling fan**. It is written in Kotlin with Jetpack Compose.

## Features

- Power, speed 1–7, 2 h / 4 h timer, Eco, LED and Reverse buttons
- Fan animation that spins faster at higher speeds
- Remembers the last power and speed state sent, across restarts
- Haptic feedback and a status indicator that flashes on each transmission
- Shows a notice on phones with no IR emitter

IR is one-way, so the app cannot read the fan's real state. The status card shows what this phone last sent, which matches the fan only while no other remote is used.

## Requirements

- A phone with an IR blaster (`android.hardware.consumerir`), such as many Xiaomi, Redmi and POCO models
- Android 8.0 (API 26) or newer

## Build

Requires JDK 17+ and the Android SDK. Set `sdk.dir` in `local.properties` (Android Studio does this for you).

```sh
./gradlew :app:testDebugUnitTest   # unit tests
./gradlew :app:installDebug        # install a debug build on a connected phone
./gradlew :app:dist                # release APK -> dist/Walton-BLDC-IR-Remote-<version>.apk
```

### Release signing

Release builds are signed using `release.jks` and `keystore.properties` in the project root. Both are git-ignored and must never be committed. `keystore.properties` looks like this:

```properties
storeFile=release.jks
storePassword=...
keyAlias=...
keyPassword=...
```

Without them, `:app:dist` produces an unsigned APK. Keep a backup of the keystore: if it is lost, updates cannot be installed over existing installs. Bump `versionCode` and `appVersionName` in `app/build.gradle.kts` for each release.

## How it works

The fan uses the NEC infrared protocol with the 16-bit (extended) address `D8 7E`, a 38 kHz carrier and LSB-first bits. `NecEncoder` turns an address and command byte into the on/off microsecond pattern that `ConsumerIrManager.transmit` expects.

| Button | Command | | Button | Command |
|---|---|---|---|---|
| Power | `08` | | Timer 2 h | `09` |
| LED | `07` | | Timer 4 h | `10` |
| Speed 1–5 | `01`–`05` | | Eco | `14` |
| Speed 6 | `15` | | Reverse | `12` |
| Speed 7 | `13` | | | |

The codes were extracted from a third-party fan remote app. See [docs/ir-codes](docs/ir-codes/README.md) for the method, the full tables and Flipper Zero export. To verify a code against a real remote, flash [docs/capture/IRCapture.ino](docs/capture/IRCapture.ino) to an Arduino with an IR receiver.

## Project layout

```
app/src/main/java/com/waltonirr/android/
  MainActivity.kt      entry point
  data/                FanButton (codes), FanState (persisted state)
  ir/                  NecEncoder, IrTransmitter (ConsumerIrManager wrapper)
  ui/                  RemoteScreen (Compose UI), FanIcons, theme/
app/src/test/          encoder unit tests
docs/                  IR code extraction and capture tooling
```

## Disclaimer

This is an unofficial project and is not affiliated with or endorsed by Walton. Use it at your own risk.
