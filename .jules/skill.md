# Modular Feature Folder Architecture Skill

## Purpose
To enable rapid modular extraction and standalone app creation for key product categories (`Audio tools`, `Doc tools`, `Video tools`, and `Device`), code for tools in these categories should reside in dedicated top-level packages under `app/src/main/java/com/toolbox/`:
- `com.toolbox.audiotools` — for all Audio tools
- `com.toolbox.doctools` — for all Document & PDF tools
- `com.toolbox.videotools` — for all Video tools
- `com.toolbox.device` — for all Device hardware, diagnostics, and phone tools

## Rules for New and Existing Tools in These Categories
1. **Dedicated Folders**: Whenever new tools or screens are added to Audio tools, Doc tools, Video tools, or Device, place them directly inside these respective packages or subpackages.
2. **Self-Contained & Modular**: Ensure files in these category packages are modular, reusable, and cleanly decoupled so they can easily be copied into a standalone app repository if needed.
3. **App Integration**: Attach screens from these packages to the main application via `ToolboxApp.kt`, `ToolDefinition.kt` (using the corresponding `ToolCategory`: `AudioTools`, `DocTools`, `VideoTools`, `Device`), and `Destinations.kt`.
