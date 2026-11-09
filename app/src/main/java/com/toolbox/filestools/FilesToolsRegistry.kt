package com.toolbox.filestools

import androidx.compose.runtime.Composable
import com.toolbox.everyday.duplicatephotos.DuplicatePhotosScreen
import com.toolbox.everyday.storageanalyzer.StorageAnalyzerScreen

object FilesToolsRegistry {
    @Composable fun DuplicatePhotos() = DuplicatePhotosScreen()
    @Composable fun StorageAnalyzer() = StorageAnalyzerScreen()
}
