package com.toolbox.devtools

import androidx.compose.runtime.Composable
import com.toolbox.conversion.devsuite.JsonFormatterScreen
import com.toolbox.conversion.devsuite.UuidGeneratorScreen
import com.toolbox.conversion.devsuite.HtmlEntityScreen
import com.toolbox.conversion.devsuite.RegexTesterScreen

object DevToolsRegistry {
    @Composable fun JsonFormatter() = JsonFormatterScreen()
    @Composable fun UuidGenerator() = UuidGeneratorScreen()
    @Composable fun HtmlEntities() = HtmlEntityScreen()
    @Composable fun RegexTester() = RegexTesterScreen()
}
