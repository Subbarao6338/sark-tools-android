package com.toolbox.videotools

import androidx.compose.runtime.Composable
import com.toolbox.everyday.screenrecorder.ScreenRecorderScreen
import com.toolbox.media.video.VideoEditorScreen

object VideoToolsRegistry {
    @Composable fun ScreenRecorder() = ScreenRecorderScreen()
    @Composable fun VideoEditor() = VideoEditorScreen()
}
