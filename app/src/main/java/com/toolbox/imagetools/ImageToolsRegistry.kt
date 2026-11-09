package com.toolbox.imagetools

import androidx.compose.runtime.Composable
import com.toolbox.media.image.ImageEditorScreen
import com.toolbox.everyday.colorpicker.ColorPickerScreen
import com.toolbox.everyday.stitcher.ScreenshotStitcherScreen
import com.toolbox.everyday.photocleanup.PhotoCleanupScreen

object ImageToolsRegistry {
    @Composable fun ImageEditor() = ImageEditorScreen()
    @Composable fun ColorPicker() = ColorPickerScreen()
    @Composable fun ScreenshotStitcher() = ScreenshotStitcherScreen()
    @Composable fun PhotoCleanup() = PhotoCleanupScreen()
}
