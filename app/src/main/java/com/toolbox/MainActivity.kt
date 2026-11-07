package com.toolbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.toolbox.core.persistence.NatureTheme
import com.toolbox.core.persistence.ThemeMode
import com.toolbox.core.persistence.UserPreferencesRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val preferencesRepository by lazy { UserPreferencesRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Count cold starts only (not config-change recreations) for the rate prompt.
        if (savedInstanceState == null) {
            lifecycleScope.launch { preferencesRepository.incrementLaunchCount() }
        }

        val launchToolId = intent?.getStringExtra("tool_id")

        setContent {
            val themeMode by preferencesRepository.themeMode
                .collectAsState(initial = ThemeMode.System)
            val natureTheme by preferencesRepository.natureTheme
                .collectAsState(initial = NatureTheme.Default)

            ToolboxApp(
                themeMode = themeMode,
                natureTheme = natureTheme,
                launchToolId = launchToolId,
                defaultScreenId = "dashboard",
            )
        }
    }
}
