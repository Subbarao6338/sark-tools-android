# All-in-One MultiTool App: Functional Specification

Clean-room spec for an original offline multitool Android app in the same product category as "Punto. – All in One MultiTool" (80+ everyday tools, offline, lightweight, 8 Indian-market languages). It is based on public store-listing facts and standard multitool functionality. It does not copy the original app's code, assets, text, branding or exact layout. Use your own name, icon and visual identity.

Known public facts about the reference app (store listing): 80+ tools in one app; offline utility; categories include calculators, unit converter, text tools, image tools; clean, fast, lightweight; min SDK 24; languages en, hi, bn, gu, kn, mr, ta, te.

---

## 1. Product goals
- One app, 80+ tools, each launches in under 300 ms, no network needed except where noted.
- No ads, no login, no analytics, no ads SDKs.
- APK under 15 MB. Cold start under 1 s on a mid-range device.
- All processing on-device.

## 2. Tech stack (recommended)
- Kotlin + Jetpack Compose (Material 3), single Activity, Navigation-Compose.
- Hilt (or manual DI), Room or DataStore for favorites/history/settings.
- minSdk 24, targetSdk 36.
- Tool registry pattern: each tool is a `Tool(id, nameRes, descRes, icon, category, keywords, screen: @Composable)` registered in one list. Home, search, favorites and recents are all driven from this registry.
- No heavy dependencies. Use platform APIs (CameraX only for scanner, ML Kit bundled only if QR scan is included).
- Localization via `strings.xml` per locale: `en, hi, bn, gu, kn, mr, ta, te`. Number formatting follows locale; offer an optional Indian digit grouping (lakh/crore).

## 3. App structure and navigation
**Bottom navigation (4 tabs):** Home · Categories · Favorites · Settings
- **Home**: search bar at top; "Recent" row (last 8 tools); "Favorites" row; "All tools" grid grouped by category.
- **Categories**: list of categories with tool count; tap opens category grid.
- **Favorites**: user-ordered list (drag to reorder), long-press to remove.
- **Settings**: see section 6.
- **Global search**: matches tool name, description and keywords in the current language and English; shows results as you type; empty state with suggestions.
- **Tool screen**: top bar with back, title, favorite star, overflow (Share result, Copy result, Reset, Help). Every tool has a "Reset" and a "Copy result" action. Results are selectable.
- Home-screen long-press app shortcuts: up to 4 (top recents or pinned).
- Optional: "Pin tool to home screen" via pinned shortcuts API.

## 4. Tool catalogue (80+)

### 4.1 Calculators (12)
1. Basic calculator (history, memory M+/M−/MR/MC, percent, backspace)
2. Scientific calculator (trig in deg/rad, log, ln, powers, roots, factorial, constants, parentheses)
3. Percentage calculator (X% of Y, X is what % of Y, % change, add/subtract %)
4. Fraction calculator (add/sub/mul/div, simplify, mixed numbers)
5. Loan / EMI calculator (principal, rate, tenure; EMI, total interest, amortization table)
6. Compound & simple interest calculator
7. SIP / investment growth calculator
8. GST / VAT calculator (add/remove tax, custom rates, CGST/SGST split)
9. Discount & sale price calculator
10. Tip & bill splitter
11. Average / mean / median / mode / range
12. Ratio & proportion solver

### 4.2 Unit converters (12)
Live two-way conversion, swap button, search units, favorite units, copy result.
13. Length 14. Weight/mass 15. Temperature 16. Area (incl. bigha, acre, cent, gunta as regional options) 17. Volume 18. Speed 19. Time 20. Data storage (decimal and binary) 21. Energy 22. Pressure 23. Fuel economy 24. Angle

### 4.3 Currency (1, optional network)
25. Currency converter using cached rates from a free public rates API; works offline with last cached rates and a visible "last updated" timestamp. Must be clearly labeled as the only online tool, or ship static rates only.

### 4.4 Text tools (14)
26. Word / character / sentence / paragraph counter (with reading time)
27. Case converter (upper, lower, title, sentence, camel, snake, kebab, toggle)
28. Remove duplicate lines / sort lines (A–Z, Z–A, numeric, by length, reverse)
29. Find & replace (plain and regex, case toggle, preview count)
30. Text diff (two inputs, side-by-side highlights)
31. Lorem ipsum generator (words/sentences/paragraphs)
32. Remove extra spaces / blank lines / line breaks
33. Text reverser / mirror
34. Slug generator
35. Number to words (English, Hindi, Indian numbering)
36. Roman numeral converter
37. Morse code encode/decode (with optional flash/vibrate playback)
38. Fancy text / unicode styles
39. Emoji and symbol picker with search and recents

### 4.5 Image tools (10) — all local
Use the system photo picker. No storage permission needed on API 33+.
40. Image compressor (quality slider, before/after size)
41. Image resizer (px/%, aspect lock, presets)
42. Image format converter (JPEG, PNG, WEBP)
43. Crop & rotate (free and fixed ratios, flip)
44. Image to PDF (multiple images, page size, margins, reorder)
45. PDF to images (page selection)
46. Color picker from image (tap pixel; HEX, RGB, HSL)
47. Image metadata (EXIF) viewer and remover
48. Collage / image stitch (vertical/horizontal)
49. Image to text (OCR) via on-device ML Kit (bundled model, optional)

### 4.6 PDF & document tools (5)
50. Merge PDFs 51. Split / extract pages 52. Compress PDF 53. Rotate pages 54. Text/notes to PDF

### 4.7 QR & codes (4)
55. QR code generator (text, URL, Wi-Fi, contact, UPI string, email, SMS, geo) with color options and save/share
56. QR / barcode scanner (CameraX, torch toggle, history, open/copy actions)
57. Barcode generator (Code128, EAN-13, UPC-A, Code39)
58. Scan history

### 4.8 Developer & encoding tools (10)
59. Base64 encode/decode (text and file)
60. URL encode/decode
61. JSON formatter / validator / minifier
62. Hash generator (MD5, SHA-1, SHA-256, SHA-512; text and file)
63. UUID generator (v4, bulk)
64. Color converter (HEX, RGB, HSL, HSV, CMYK) with picker and palette
65. HTML entity encode/decode
66. Timestamp ↔ date converter (Unix epoch, timezones)
67. Regex tester (live match highlighting)
68. Binary / hex / octal / decimal base converter

### 4.9 Date & time (8)
69. Age calculator (years, months, days, next birthday)
70. Date difference calculator
71. Add / subtract days to date
72. Working days calculator
73. World clock (add cities, offsets)
74. Stopwatch (laps, background-safe via foreground service)
75. Countdown timer (multiple timers, notification, alarm sound)
76. Time zone converter

### 4.10 Health & lifestyle (6)
77. BMI calculator 78. Calorie / BMR calculator 79. Water intake reminder 80. Pregnancy due date calculator 81. Ideal weight calculator 82. Sleep cycle calculator

### 4.11 Generators & randomizers (7)
83. Password generator (length, charsets, ambiguity filter, strength meter, copy and auto-clear clipboard)
84. Random number generator 85. Dice roller 86. Coin flip 87. Spin the wheel / decision maker 88. Random name/list picker 89. Passphrase generator

### 4.12 Device & utilities (10)
90. Flashlight (with SOS and strobe) 91. Compass 92. Spirit level 93. Ruler (on-screen, cm/in, calibration) 94. Protractor 95. Sound meter (dB) 96. Device info (model, OS, RAM, storage, battery, display) 97. Battery info 98. Screen test (dead pixels: solid color cycle) 99. Notes / checklist (local Room storage, pin, color labels)

> Treat the list as a baseline. You can drop or swap any tool; keep the count at 80+ and the registry architecture unchanged.

## 5. UI / UX specification
- **Design system**: Material 3, dynamic color (Material You) on Android 12+, static brand palette fallback.
- **Themes**: System / Light / Dark / AMOLED black.
- **Home grid**: 4 columns phone, 6–8 tablet; each tile = icon (24 dp in tonal circle) + label (max 2 lines).
- **Category color coding**: each category has an accent tone used on icons and chips.
- **Typography**: system font with Material 3 type scale; support font scaling to 200%.
- **Inputs**: numeric tools use a custom numeric keypad option; all fields show units, validation errors inline, and keep state across rotation/process death (`rememberSaveable` / SavedStateHandle).
- **Results**: large, selectable, one-tap copy, share via system sheet.
- **Haptics**: light tick on keypad and toggles; setting to disable.
- **Empty/error states**: icon + one line + action.
- **Accessibility**: TalkBack labels on all icons, minimum 48 dp targets, contrast AA, no color-only meaning.
- **Tablets/foldables**: adaptive layouts (list-detail for Categories, two-pane for calculators with history).
- **Animations**: shared-element transition from tile to tool (optional), respect "remove animations".
- **Onboarding**: none, or a single dismissible hint card on Home about search and favorites.

## 6. Settings
- Language (system default + 8 locales), Theme, Dynamic color toggle, AMOLED toggle
- Number format (locale / Indian / international), decimal places (0–10), angle default (deg/rad)
- Default home layout (grid / list), tile size
- Haptics, keep screen on in tools, auto-clear clipboard timeout
- Clear recents / history / cached data
- Backup & restore favorites and notes to a local JSON file
- About: version, open-source licenses, privacy policy, feedback email, rate app

## 7. Data and storage
- DataStore: settings, favorites order, recents.
- Room: notes, calculator history, scan history, timers.
- Files only via Storage Access Framework or MediaStore; no broad storage permission.
- No network permission unless the currency tool is included, in which case it is the only use.

## 8. Permissions (request in context, only when a tool is opened)
- CAMERA (scanner, flashlight via torch API needs none)
- POST_NOTIFICATIONS (timers, water reminder)
- FOREGROUND_SERVICE (stopwatch/timer) and SCHEDULE_EXACT_ALARM only if needed
- RECORD_AUDIO (sound meter)
- ACCESS_FINE_LOCATION not needed; compass uses sensors only
- VIBRATE

## 9. Non-functional requirements
- Offline-first; graceful degradation for currency.
- No crash on rotation, dark mode switch, locale change, or low memory.
- R8 shrinking and resource shrinking enabled; split APKs/AAB by ABI, density and language.
- Unit tests for every calculator/converter (table-driven); UI tests for navigation, search, favorites.
- Edge cases: division by zero, very large numbers (BigDecimal), invalid dates, empty input, RTL-safe layouts.

## 10. Build and CI (GitHub Actions)
```yaml
name: Android CI
on: [push, workflow_dispatch]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 17 }
      - uses: gradle/actions/setup-gradle@v4
      - run: ./gradlew testDebugUnitTest assembleRelease bundleRelease
      - uses: actions/upload-artifact@v4
        with:
          name: apk
          path: app/build/outputs/apk/release/*.apk
```
Store the keystore and passwords as GitHub Secrets and sign in the release step.

## 11. Delivery milestones
1. Registry, navigation, theming, i18n scaffolding, Home/Search/Favorites
2. Calculators + converters (tools 1–25)
3. Text + developer tools (26–39, 59–68)
4. Date/time + health + generators (69–89)
5. Image, PDF, QR tools (40–58)
6. Device utilities, polish, accessibility, localization review, CI release

## 12. What this spec cannot tell you
The reference app's exact tool list, screen layouts, wording and icons are not in public listings. If you want a closer match to what you like about it, send screenshots or a list of its tools from the app's own Play Store page, and I'll revise the catalogue to match the functionality (still with an original design and code).
