package com.toolbox.mediaconverters

import androidx.compose.runtime.Composable
import com.toolbox.media.imagesuite.ImageResizerScreen
import com.toolbox.media.imagesuite.ImageFormatConverterScreen

object MediaConvertersRegistry {
    @Composable fun ImageResizer() = ImageResizerScreen()
    @Composable fun ImageFormatConverter() = ImageFormatConverterScreen()
}
