package com.toolbox.programmingtools

import androidx.compose.runtime.Composable
import com.toolbox.conversion.numberbase.NumberBaseScreen
import com.toolbox.everyday.hashencode.HashEncodeScreen

object ProgrammingToolsRegistry {
    @Composable fun NumberBase() = NumberBaseScreen()
    @Composable fun HashEncode() = HashEncodeScreen()
}
