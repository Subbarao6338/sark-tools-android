# Modular Feature Folder Architecture Skill

## Purpose
To enable rapid modular extraction and standalone app creation for any product category, code for tools across all categories should be organized into dedicated modular top-level packages under `app/src/main/java/com/toolbox/`.

Each category package serves as a self-contained module or provides a dedicated registry (`*Registry.kt`) aggregating the category's composables, dependencies, and screens for clean standalone decoupling.

## Standard Category Packages Under `com.toolbox.*`
- `com.toolbox.apptools` — App Tools (`widgets_manager`, `quick_tiles_manager`, `shortcuts_manager`, etc.)
- `com.toolbox.audiotools` — Audio Tools (`sound_meter`, `pitch_tuner`, `metronome`, `white_noise`, `spectrum_analyzer`, etc.)
- `com.toolbox.conversion` / `com.toolbox.unitconverters` — Unit Converters & General Converters
- `com.toolbox.calculators` — Mathematical, Financial, Health & Geometry Calculators
- `com.toolbox.datetime` — Date & Time Tools (`date_calculator`, `stopwatch_timer`, `world_clock`, `focus_timer`, etc.)
- `com.toolbox.device` — Device hardware, diagnostics, and phone tools (`device_info`, `battery_info`, `screen_grid`, etc.)
- `com.toolbox.devtools` — Developer Tools (`json_formatter`, `uuid_generator`, `regex_tester`, `html_entity_tool`, etc.)
- `com.toolbox.doctools` — Document & PDF Tools (`pdf_toolkit`, `document_scanner`, `pdf_to_images`, `compress_pdf`, etc.)
- `com.toolbox.education` — Educational, Periodic Table & Science Tools
- `com.toolbox.everyday` — Everyday Utility Tools (`flashlight`, `counter`, `password_generator`, etc.)
- `com.toolbox.filestools` — Files & Storage Management Tools (`storage_analyzer`, `duplicate_photos`, etc.)
- `com.toolbox.games` — Games & Randomizers (`spin_wheel`, `random_name_picker`, `pokedex`, etc.)
- `com.toolbox.imagetools` — Image Editing & Viewer Tools (`image_editor`, `color_picker`, `screenshot_stitcher`, etc.)
- `com.toolbox.mediaconverters` — Media Format & Dimension Converters (`image_resizer`, `image_format_converter`, etc.)
- `com.toolbox.networktools` — Network Diagnostics & Connectivity Tools (`wifi_signal`, `network_info`, `network_speed_test`, `web_tools`, `wifi_qr_share`, etc.)
- `com.toolbox.programmingtools` — Programming Utilities (`hash_encode`, `number_base`, etc.)
- `com.toolbox.sensors` / `com.toolbox.measurement` — Hardware Sensor Tools (`level`, `compass`, `protractor`, `vibrometer`, etc.)
- `com.toolbox.social` — Social Media Tools (`hashtag_generator`, `social_bio`, `social_preview`, `quote_maker`, `status_saver`, etc.)
- `com.toolbox.videotools` — Video Tools (`screen_recorder`, `video_editor`, etc.)
- `com.toolbox.ai` — AI Hub & Generative AI Tools

## Universal Rules for All Categories (Existing & Any Future Categories)
1. **Dedicated Modular Folders & Registries**:
   - Whenever any category is defined or added (e.g., `AppTools`, `DevTools`, `ProgrammingTools`, `FilesTools`, `NetworkTools`, `MediaConverters`, or any future category), ensure there is a dedicated top-level package `com.toolbox.<categoryname_lowercase>` and a corresponding registry object `<CategoryName>Registry.kt`.
   - The category registry provides zero-friction access points for all composable tools belonging to that category.
2. **Self-Contained & Modular**:
   - Files in each category package must be decoupled from other feature domains so they can easily be copied into a standalone app repository if needed.
   - Core shared utilities (e.g., permissions, UI theme, base scaffolds) should be consumed only from `com.toolbox.core.*`.
3. **Seamless Dashboard & Navigation Integration**:
   - Every category must be represented in `ToolCategory` enum with appropriate label, default tile color, icon tint, and theme bindings in `themeTileColor()` and `themeIconTint()`.
   - Tool definitions in `allTools` must specify their correct category assignment.
   - Any screen route must be registered in `Destinations.kt` and wired into `ToolboxApp.kt`.
4. **Future-Proofing New Categories**:
   - Whenever a new category is introduced in the future:
     1. Add the enum constant in `ToolCategory` with label and theme bindings.
     2. Create `com.toolbox.<categoryname_lowercase>/<CategoryName>Registry.kt`.
     3. Assign existing or newly implemented tools to the category.
     4. Document the category in `.jules/skill.md`.
