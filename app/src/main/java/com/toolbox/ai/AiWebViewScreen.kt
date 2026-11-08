package com.toolbox.ai

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.key
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.toolbox.core.ui.copyToClipboard

data class WebProfile(
    val id: String,
    val name: String,
    val description: String,
    val isIncognito: Boolean = false,
    val customUserAgent: String? = null,
    val isDefault: Boolean = false,
)

object WebProfileManager {
    val predefinedProfiles = listOf(
        WebProfile(
            id = "default",
            name = "Default Profile",
            description = "Standard browsing session with cookies & history preserved.",
            isDefault = true,
        ),
        WebProfile(
            id = "work",
            name = "Work / Projects",
            description = "Dedicated profile for AI development, coding, and APIs.",
        ),
        WebProfile(
            id = "personal",
            name = "Personal",
            description = "Personal creative projects, prompts, and stories.",
        ),
        WebProfile(
            id = "anonymous",
            name = "Incognito / Private",
            description = "Ephemeral session with zero telemetry, cache cleared on exit.",
            isIncognito = true,
        ),
    )

    private const val PREFS_NAME = "ai_web_profiles"
    private const val KEY_ACTIVE_PROFILE_PREFIX = "active_profile_"

    fun getActiveProfileId(context: Context, toolKey: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_PROFILE_PREFIX + toolKey, "default") ?: "default"
    }

    fun setActiveProfileId(context: Context, toolKey: String, profileId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ACTIVE_PROFILE_PREFIX + toolKey, profileId).apply()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiWebViewScreen(
    initialUrl: String,
    toolKey: String,
    toolName: String,
    defaultPresets: List<Pair<String, String>> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val profiles = remember { mutableStateListOf<WebProfile>().apply { addAll(WebProfileManager.predefinedProfiles) } }

    var activeProfileId by rememberSaveable {
        mutableStateOf(WebProfileManager.getActiveProfileId(context, toolKey))
    }

    val activeProfile = profiles.find { it.id == activeProfileId } ?: profiles.first()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by rememberSaveable { mutableStateOf(initialUrl) }
    var pageTitle by rememberSaveable { mutableStateOf(toolName) }
    var canGoBack by rememberSaveable { mutableStateOf(false) }
    var canGoForward by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var loadProgress by rememberSaveable { mutableFloatStateOf(0f) }
    var showProfileDialog by rememberSaveable { mutableStateOf(false) }
    var showAddProfileDialog by rememberSaveable { mutableStateOf(false) }
    var newProfileName by rememberSaveable { mutableStateOf("") }
    var newProfileDesc by rememberSaveable { mutableStateOf("") }
    var newProfileIncognito by rememberSaveable { mutableStateOf(false) }

    // Key to force recreating/reloading WebView on profile switch
    var webViewKey by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = canGoBack) {
        webViewInstance?.goBack()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top Toolbar: Profile Switcher & Action Controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Profile Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (activeProfile.isIncognito) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showProfileDialog = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = if (activeProfile.isIncognito) Icons.Default.Shield else Icons.Default.Person,
                                contentDescription = "Active Profile",
                                modifier = Modifier.size(16.dp),
                                tint = if (activeProfile.isIncognito) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = activeProfile.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (activeProfile.isIncognito) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    // Quick Presets or Navigation Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { webViewInstance?.goBack() },
                            enabled = canGoBack,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(
                            onClick = { webViewInstance?.goForward() },
                            enabled = canGoForward,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Forward",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(
                            onClick = {
                                if (isLoading) webViewInstance?.stopLoading() else webViewInstance?.reload()
                            },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                                contentDescription = if (isLoading) "Stop" else "Reload",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        IconButton(
                            onClick = {
                                webViewInstance?.loadUrl(initialUrl)
                            },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = {
                                copyToClipboard(context, "Page URL", currentUrl)
                            },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL", modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in External Browser", modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // URL & SSL indicator
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (currentUrl.startsWith("https://")) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Secure",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(
                            text = currentUrl,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            LinearProgressIndicator(
                progress = { loadProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // Preset quick jump links bar (if available)
        if (defaultPresets.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                defaultPresets.forEach { (label, url) ->
                    FilledTonalButton(
                        onClick = { webViewInstance?.loadUrl(url) },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp),
                    ) {
                        Text(label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Android WebView container
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            key(webViewKey) {
                AndroidView(
                    factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                            cacheMode = if (activeProfile.isIncognito) {
                                WebSettings.LOAD_NO_CACHE
                            } else {
                                WebSettings.LOAD_DEFAULT
                            }
                            userAgentString = userAgentString.replace("; wv", "")
                        }

                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)

                        if (activeProfile.isIncognito) {
                            clearCache(true)
                            clearHistory()
                            clearFormData()
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                                url?.let { currentUrl = it }
                                canGoBack = canGoBack()
                                canGoForward = canGoForward()
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                url?.let { currentUrl = it }
                                pageTitle = view?.title ?: toolName
                                canGoBack = canGoBack()
                                canGoForward = canGoForward()
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val reqUrl = request?.url?.toString() ?: return false
                                return if (reqUrl.startsWith("http://") || reqUrl.startsWith("https://")) {
                                    false
                                } else {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reqUrl)).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                        true
                                    } catch (_: Exception) {
                                        true
                                    }
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadProgress = newProgress / 100f
                                if (newProgress == 100) isLoading = false
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                title?.let { if (it.isNotBlank()) pageTitle = it }
                            }
                        }

                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                update = { wv ->
                    webViewInstance = wv
                },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    // Profile Selector Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Select Web Profile", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Profiles keep separate sessions, credentials, and cookies for different AI workloads.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(profiles) { profile ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeProfileId = profile.id
                                        WebProfileManager.setActiveProfileId(context, toolKey, profile.id)
                                        webViewKey += 1
                                        showProfileDialog = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (profile.id == activeProfileId) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainerHigh
                                    },
                                ),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = profile.name,
                                                fontWeight = FontWeight.SemiBold,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            if (profile.isIncognito) {
                                                Spacer(Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.errorContainer,
                                                ) {
                                                    Text(
                                                        "Private",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = profile.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    if (profile.id == activeProfileId) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showProfileDialog = false
                            showAddProfileDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Custom Profile")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close")
                }
            },
        )
    }

    // Add Custom Profile Dialog
    if (showAddProfileDialog) {
        AlertDialog(
            onDismissRequest = { showAddProfileDialog = false },
            title = { Text("New Web Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newProfileName,
                        onValueChange = { newProfileName = it },
                        label = { Text("Profile Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = newProfileDesc,
                        onValueChange = { newProfileDesc = it },
                        label = { Text("Description (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { newProfileIncognito = !newProfileIncognito },
                    ) {
                        RadioButton(selected = newProfileIncognito, onClick = { newProfileIncognito = !newProfileIncognito })
                        Spacer(Modifier.width(8.dp))
                        Text("Incognito / Clear cache on exit", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newProfileName.isNotBlank()) {
                            val newProfile = WebProfile(
                                id = "custom_${System.currentTimeMillis()}",
                                name = newProfileName.trim(),
                                description = newProfileDesc.ifBlank { "Custom user profile" },
                                isIncognito = newProfileIncognito,
                            )
                            profiles.add(newProfile)
                            activeProfileId = newProfile.id
                            WebProfileManager.setActiveProfileId(context, toolKey, newProfile.id)
                            webViewKey += 1
                            newProfileName = ""
                            newProfileDesc = ""
                            newProfileIncognito = false
                            showAddProfileDialog = false
                        }
                    },
                    enabled = newProfileName.isNotBlank(),
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProfileDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    DisposableEffect(activeProfileId) {
        onDispose {
            if (activeProfile.isIncognito) {
                webViewInstance?.apply {
                    clearCache(true)
                    clearHistory()
                    clearFormData()
                }
            }
        }
    }
}
