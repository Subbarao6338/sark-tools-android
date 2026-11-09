package com.toolbox.apptools

import androidx.compose.runtime.Composable
import com.toolbox.widgets.WidgetsManagementScreen
import com.toolbox.tiles.QuickTilesManagementScreen
import com.toolbox.core.shortcuts.ShortcutsManagementScreen

object AppToolsRegistry {
    @Composable fun WidgetsManager(onBack: () -> Unit = {}) = WidgetsManagementScreen(onBack = onBack)
    @Composable fun QuickTilesManager(onBack: () -> Unit = {}) = QuickTilesManagementScreen(onBack = onBack)
    @Composable fun ShortcutsManager(onBack: () -> Unit = {}) = ShortcutsManagementScreen(onBack = onBack)
}
