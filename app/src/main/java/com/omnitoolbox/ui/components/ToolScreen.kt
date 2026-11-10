package omni.toolbox.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Shortcut
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolScreen(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    toolRoute: String? = null,
    containerColor: Color = Color.Transparent,
    showTopBar: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val resolvedContainerColor = if (containerColor == Color.Transparent) {
        MaterialTheme.colorScheme.background
    } else {
        containerColor
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        containerColor = resolvedContainerColor,
        topBar = {
            if (showTopBar) {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (toolRoute != null) {
                            val context = LocalContext.current
                            IconButton(onClick = {
                                val tool = omni.toolbox.model.ToolProvider.tools.find { it.route == toolRoute }
                                if (tool != null) {
                                    omni.toolbox.utils.ShortcutUtils.pinShortcut(context, tool)
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Shortcut, contentDescription = "Pin to Home")
                            }
                        }
                        actions()
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = resolvedContainerColor
                    )
                )
            }
        },
        floatingActionButton = {
            Box(modifier = Modifier.navigationBarsPadding().padding(bottom = 16.dp, end = 8.dp)) {
                floatingActionButton()
            }
        }
    ) { padding ->
        val adjustedPadding = PaddingValues(
            start = padding.calculateStartPadding(LocalLayoutDirection.current),
            top = padding.calculateTopPadding(),
            end = padding.calculateEndPadding(LocalLayoutDirection.current),
            bottom = padding.calculateBottomPadding()
        )
        content(adjustedPadding)
    }
}
