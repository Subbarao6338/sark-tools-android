# Sark Tools — Precision Android Utility Suite

A privacy-respecting, offline-first Android utility suite with 60+ tools across sensors, calculations, everyday utilities, converters, science education, and games. Built with Kotlin, Jetpack Compose, and Material 3.

## Highlights
- **APK Format**: Released APK name format follows `sark-tools-<version>.apk`.
- **Languages**: Strictly English and Telugu (`te`) localization.
- **Nature Themes**: Forest Emerald, Ocean Breeze, Sunset Dunes, Cherry Blossom, and Lavender Mist themes with dynamic color fallback.
- **Modern UI/UX**: Category tool count badges, real-time live filtering, category tile layouts, and hero dashboard analytics.
- **Zero Tracking**: 100% offline-first, no analytics, no ads, and no external network dependencies.

## Screenshots

| Dashboard | Bubble Level | Compass | Settings | Favorites |
|:---------:|:------------:|:-------:|:--------:|:---------:|
| ![Dashboard](docs/screenshots/home.png) | ![Bubble Level](docs/screenshots/bubble-level.png) | ![Compass](docs/screenshots/compass.png) | ![Settings](docs/screenshots/settings.png) | ![Favorites](docs/screenshots/favorites.png) |

## Features

### Science, Education & Games
| Tool | Description |
|------|-------------|
| **Periodic Table** | Full 118-element interactive periodic table with 18-column grid view, list view, category and state filters, real-time search, and detailed chemical/physical property sheets. |
| **Chemistry Games & Lab** | Element Symbol Quiz, Atomic Number Challenge, and interactive Compound Synthesis Lab with stoichiometric balancing. |
| **Pokédex** | Complete National Pokédex with 898 Pokémon across all generations. Real-time search, dual-type filtering, stat rankings (HP, Atk, Spd, Total Base Stats), lore descriptions, and "Who's That Pokémon?" guessing mini-game. |
| **STEM Science Labs** | Physics Harmonic Pendulum and 2D Projectile Motion trajectory simulator; Biology DNA/RNA codon-to-amino acid polypeptide chain synthesizer and Mendelian Punnett Square genetics simulator. |
| **Brain & IQ Games** | Speed Math arithmetic workout, IQ number sequence pattern solver, 4x4 Visual Memory Matrix recall game, and General Science & World Trivia Quiz. |

### Measurement
| Tool | Description |
|------|-------------|
| Bubble Level | Circular + linear level with pitch/roll readout |
| Compass | Compass rose with magnetic/true north toggle |
| Protractor | On-screen angle measurement |
| Sound Meter | Live dB meter with min/avg/max stats |
| Ruler | Full-width ruler with cm/in toggle and credit-card calibration |
| Light Meter | Ambient lux sensor readout |
| Barometer | Atmospheric pressure via pressure sensor |
| Altitude | Elevation estimate from barometric pressure |
| Gyroscope | Real-time angular velocity on all three axes |
| Humidity | Relative humidity (on supported hardware) |
| Metal Detector | Magnetic field strength as a proximity indicator |
| Speedometer | GPS-based speed with unit toggle |
| Vibrometer | Vibration intensity from accelerometer |
| Spectrum Analyzer | Real-time audio frequency spectrum via FFT |

### Conversion
| Tool | Description |
|------|-------------|
| Unit Converter | 17+ dedicated multi-unit conversion screens across length, weight, temperature, volume, area, speed, pressure, energy, power, and more |
| Percentage Calculator | Percent of, percent change, and reverse calculations |
| Tip Calculator | Bill splitting with custom tip % |
| Number Base | Convert between binary, octal, decimal, and hex |
| Currency Converter | Offline benchmark currency conversion |

### Everyday
| Tool | Description |
|------|-------------|
| QR Scanner | Scan barcodes/QR codes and generate QR codes from text |
| Counter | Persistent multi-step counter with DataStore persistence |
| Stopwatch & Timer | Stopwatch with laps; countdown timer with background notification |
| Random | Dice rolls, coin flip, and random number generator |
| Color Picker | Sample any color from the camera feed |
| Magnifier | Camera-based magnifier with zoom control |
| Mirror | Front camera as a mirror with true mirror mode |
| Heart Rate | Estimate BPM via rear camera finger placement |
| Notepad | Quick scratchpad with automated backup persistence |
| Password Generator | Customizable strong password generator |

### Other
| Tool | Description |
|------|-------------|
| Flashlight | Torch with brightness control, SOS mode, and strobe |
| Pedometer | Step counter using the hardware step sensor |
| Battery Health | Monitor battery voltage, temperature, technology, and health recommendations |

## Platform Features

- **Quick Settings Tiles** — 25+ Quick Settings tiles directly from the notification shade
- **Interactive Widgets** — 10+ Android home screen Glance widgets including Counter, Battery, Quick Launch, and Stopwatch
- **App Shortcuts** — Instant access to frequent tools from launcher long-press menu
- **Favorites** — Pin any tool to a customizable favorites screen
- **Backup & Restore** — Offline JSON-based export and import of user data, preferences, notes, counters, and favorites
- **Share Measurements** — Export a measurement card image from any sensor tool
- **Dynamic theming** — Material You color system on Android 12+, static fallback on older devices
- **Predictive back** — Full support for Android 14 predictive back gesture

## Tech Stack

- **Language**: Kotlin 2.0+
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Type-safe Navigation Compose 2.8+ (`@Serializable` routes)
- **Camera**: CameraX 1.6+ with `camera-compose` viewfinder
- **QR**: ZXing core 3.5.3 (custom `ImageAnalysis.Analyzer`, no external Activity)
- **Persistence**: DataStore Preferences (counter state, favorites, theme)
- **Timer**: `AlarmManager.setExactAndAllowWhileIdle()` + foreground service for live notification
- **Sensors**: `DisposableEffect` + `LifecycleEventObserver` with low-pass filtering
- **Min SDK**: 26 (Android 8.0) · **Target SDK**: 36

## Architecture

Single-module, package-per-feature layout. No DI framework — manual `ViewModelProvider.Factory` injection kept simple for MVP scale.

```
app/
├── core/          # Shared — sensors, camera, permissions, UI components, sharing, persistence
├── dashboard/     # Home screen grid with search and category filtering
├── education/     # Periodic Table, Chemistry Games, STEM Labs, Brain & IQ Games
├── pokedex/       # Complete 898-entry National Pokédex and silhouette guessing game
├── measurement/   # Sensor-based measurement tools
├── conversion/    # Unit/math conversion tools and dedicated multi-unit converters
├── everyday/      # Camera, general-purpose utilities, and focus tools
├── lighting/      # Flashlight and screen test patterns
├── motion/        # Pedometer
├── favorites/     # Pinned tools screen
├── settings/      # Theme, sorting + backup/restore preferences
├── tiles/         # Quick Settings tile services
├── widgets/       # Android Glance Home Screen widgets
└── nav/           # Navigation destinations
```

## Building

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease
```

## Privacy

- No analytics, no crash reporting, no ads
- No network requests — fully offline
- Runtime permissions are requested lazily, only when the relevant tool is opened
- All sensor and user data stays strictly on-device

## License

MIT
