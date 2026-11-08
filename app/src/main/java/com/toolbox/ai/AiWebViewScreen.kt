package com.toolbox.ai

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.JsResult
import android.webkit.ServiceWorkerController
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.toolbox.core.shortcuts.FavoriteShortcutManager
import com.toolbox.core.ui.copyToClipboard
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    onBack: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profiles = remember { mutableStateListOf<WebProfile>().apply { addAll(WebProfileManager.predefinedProfiles) } }

    var activeProfileId by rememberSaveable {
        mutableStateOf(WebProfileManager.getActiveProfileId(context, toolKey))
    }

    val activeProfile = profiles.find { it.id == activeProfileId } ?: profiles.first()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by rememberSaveable { mutableStateOf(initialUrl) }
    var pageTitle by rememberSaveable { mutableStateOf(toolName) }
    var pageFavicon by remember { mutableStateOf<Bitmap?>(null) }
    var canGoBack by rememberSaveable { mutableStateOf(false) }
    var canGoForward by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var loadProgress by rememberSaveable { mutableFloatStateOf(0f) }
    var isDesktopMode by rememberSaveable { mutableStateOf(false) }

    // Floating menu state
    var showFloatingMenu by rememberSaveable { mutableStateOf(false) }
    var showProfileDialog by rememberSaveable { mutableStateOf(false) }
    var showAddProfileDialog by rememberSaveable { mutableStateOf(false) }
    var newProfileName by rememberSaveable { mutableStateOf("") }
    var newProfileDesc by rememberSaveable { mutableStateOf("") }
    var newProfileIncognito by rememberSaveable { mutableStateOf(false) }

    // FAB dragging offset
    var fabOffsetX by remember { mutableFloatStateOf(0f) }
    var fabOffsetY by remember { mutableFloatStateOf(0f) }

    // File Chooser for PWA / Web File Uploads
    var filePathCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val uris: Array<Uri>? = if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            when {
                data?.clipData != null -> {
                    val count = data.clipData!!.itemCount
                    Array(count) { i -> data.clipData!!.getItemAt(i).uri }
                }
                data?.data != null -> arrayOf(data.data!!)
                else -> null
            }
        } else null
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    // Fullscreen Custom View (HTML5 video / game fullscreen)
    var customView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    // JavaScript Dialogs state
    var jsAlertDialogMessage by remember { mutableStateOf<String?>(null) }
    var jsAlertCallback by remember { mutableStateOf<JsResult?>(null) }

    var jsConfirmDialogMessage by remember { mutableStateOf<String?>(null) }
    var jsConfirmCallback by remember { mutableStateOf<JsResult?>(null) }

    // Key to force recreating/reloading WebView on profile switch or desktop mode
    var webViewKey by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = true) {
        when {
            customView != null -> {
                customViewCallback?.onCustomViewHidden()
                customView = null
                customViewCallback = null
            }
            canGoBack -> {
                webViewInstance?.goBack()
            }
            onBack != null -> {
                onBack()
            }
        }
    }

    // Frameless Website Container respecting system status bar and nav bar
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Fullscreen Custom View overlay (if active)
        if (customView != null) {
            AndroidView(
                factory = { customView!! },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                // Android WebView container respecting status bar and nav bar
                key(webViewKey) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )

                                // Hardware acceleration
                                setLayerType(View.LAYER_TYPE_HARDWARE, null)

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
                                    allowFileAccess = true
                                    allowContentAccess = true

                                    // Aggressive Caching & Instant Loading Optimization
                                    if (activeProfile.isIncognito) {
                                        cacheMode = WebSettings.LOAD_NO_CACHE
                                    } else {
                                        cacheMode = WebSettings.LOAD_DEFAULT
                                    }

                                    // High render performance
                                    @Suppress("DEPRECATION")
                                    setRenderPriority(WebSettings.RenderPriority.HIGH)
                                    offscreenPreRaster = true
                                    mediaPlaybackRequiresUserGesture = false

                                    // PWA and ServiceWorker setup
                                    javaScriptCanOpenWindowsAutomatically = true

                                    // Desktop vs Mobile UA
                                    val defaultUa = userAgentString.replace("; wv", "")
                                    userAgentString = if (isDesktopMode) {
                                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
                                    } else {
                                        defaultUa
                                    }
                                }

                                // ServiceWorker Cache & Instant Offline PWA loading
                                try {
                                    val swController = ServiceWorkerController.getInstance()
                                    swController.serviceWorkerWebSettings.apply {
                                        allowContentAccess = true
                                        allowFileAccess = true
                                        cacheMode = if (activeProfile.isIncognito) {
                                            WebSettings.LOAD_NO_CACHE
                                        } else {
                                            WebSettings.LOAD_DEFAULT
                                        }
                                    }
                                } catch (_: Exception) {}

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
                                        if (favicon != null) pageFavicon = favicon
                                        canGoBack = canGoBack()
                                        canGoForward = canGoForward()
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        isLoading = false
                                        url?.let { currentUrl = it }
                                        pageTitle = view?.title ?: toolName
                                        if (view?.favicon != null) pageFavicon = view.favicon
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

                                    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                                        if (icon != null) pageFavicon = icon
                                    }

                                    // Fullscreen HTML5 Video/View Support
                                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                        customView = view
                                        customViewCallback = callback
                                    }

                                    override fun onHideCustomView() {
                                        customView = null
                                        customViewCallback?.onCustomViewHidden()
                                        customViewCallback = null
                                    }

                                    // PWA File Upload Chooser
                                    override fun onShowFileChooser(
                                        webView: WebView?,
                                        filePathCallbackParam: ValueCallback<Array<Uri>>?,
                                        fileChooserParams: FileChooserParams?,
                                    ): Boolean {
                                        filePathCallback?.onReceiveValue(null)
                                        filePathCallback = filePathCallbackParam
                                        val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                            type = "*/*"
                                        }
                                        try {
                                            filePickerLauncher.launch(intent)
                                        } catch (e: Exception) {
                                            filePathCallback?.onReceiveValue(null)
                                            filePathCallback = null
                                            return false
                                        }
                                        return true
                                    }

                                    // HTML5 Geolocation
                                    override fun onGeolocationPermissionsShowPrompt(
                                        origin: String?,
                                        callback: GeolocationPermissions.Callback?,
                                    ) {
                                        callback?.invoke(origin, true, false)
                                    }

                                    // HTML5 Media Permissions (Camera, Mic)
                                    override fun onPermissionRequest(request: PermissionRequest?) {
                                        request?.grant(request.resources)
                                    }

                                    // JavaScript Alert
                                    override fun onJsAlert(
                                        view: WebView?,
                                        url: String?,
                                        message: String?,
                                        result: JsResult?,
                                    ): Boolean {
                                        jsAlertDialogMessage = message ?: ""
                                        jsAlertCallback = result
                                        return true
                                    }

                                    // JavaScript Confirm
                                    override fun onJsConfirm(
                                        view: WebView?,
                                        url: String?,
                                        message: String?,
                                        result: JsResult?,
                                    ): Boolean {
                                        jsConfirmDialogMessage = message ?: ""
                                        jsConfirmCallback = result
                                        return true
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

                // Top Thin Instant Loading Progress Indicator (at top of web view content)
                if (isLoading) {
                    LinearProgressIndicator(
                        progress = { loadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.TopCenter),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent,
                    )
                }

                // Draggable Floating Options Menu Button (Floating Bubble FAB)
                FloatingActionButton(
                    onClick = { showFloatingMenu = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .offset { IntOffset(fabOffsetX.roundToInt(), fabOffsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                fabOffsetX += dragAmount.x
                                fabOffsetY += dragAmount.y
                            }
                        },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Web Options Menu",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet Floating Options Menu
    if (showFloatingMenu) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showFloatingMenu = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header with current page title and SSL status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pageTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                    // Close menu
                    IconButton(onClick = { showFloatingMenu = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close Menu")
                    }
                }

                HorizontalDivider()

                // Row 1: Primary Navigation Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            webViewInstance?.goBack()
                            showFloatingMenu = false
                        },
                        enabled = canGoBack,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    IconButton(
                        onClick = {
                            webViewInstance?.goForward()
                            showFloatingMenu = false
                        },
                        enabled = canGoForward,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
                    }
                    IconButton(
                        onClick = {
                            if (isLoading) webViewInstance?.stopLoading() else webViewInstance?.reload()
                            showFloatingMenu = false
                        },
                    ) {
                        Icon(
                            if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                            contentDescription = if (isLoading) "Stop" else "Reload",
                        )
                    }
                    IconButton(
                        onClick = {
                            webViewInstance?.loadUrl(initialUrl)
                            showFloatingMenu = false
                        },
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                }

                // Row 2: Active Web Profile Card & Switcher
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showFloatingMenu = false
                            showProfileDialog = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (activeProfile.isIncognito) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (activeProfile.isIncognito) Icons.Default.Shield else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (activeProfile.isIncognito) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = activeProfile.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeProfile.isIncognito) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                Text(
                                    text = if (activeProfile.isIncognito) "Incognito · Zero Telemetry" else "Isolated Session & Cache",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (activeProfile.isIncognito) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                )
                            }
                        }
                        Text(
                            text = "Switch",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (activeProfile.isIncognito) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }

                // Preset quick jump links bar (if available)
                if (defaultPresets.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Quick Hub Presets",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            defaultPresets.forEach { (label, url) ->
                                FilledTonalButton(
                                    onClick = {
                                        webViewInstance?.loadUrl(url)
                                        showFloatingMenu = false
                                    },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp),
                                ) {
                                    Text(label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                // PWA & Enhanced Web Options
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Install PWA / Add to Home Screen
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                showFloatingMenu = false
                                val pinned = FavoriteShortcutManager.pinWebPwaShortcut(
                                    context = context,
                                    toolId = toolKey,
                                    pageTitle = pageTitle.take(20),
                                    pageUrl = currentUrl,
                                    favicon = pageFavicon,
                                    accentColor = 0xFF1976D2.toInt(),
                                )
                                if (pinned) {
                                    Toast.makeText(context, "Added $pageTitle to Home Screen", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Shortcut pinning not supported by launcher", Toast.LENGTH_SHORT).show()
                                }
                            },
                        color = Color.Transparent,
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.AddHome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("Install PWA / Add to Home Screen", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Pin web app with icon & dedicated shortcut", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Desktop / Mobile Mode Toggle
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                isDesktopMode = !isDesktopMode
                                webViewKey += 1
                                showFloatingMenu = false
                            },
                        color = Color.Transparent,
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Devices, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(if (isDesktopMode) "Switch to Mobile View" else "Request Desktop Site", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(if (isDesktopMode) "Currently Desktop User-Agent" else "Currently Mobile User-Agent", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Instant Fast Reload / Clear Cache
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                showFloatingMenu = false
                                webViewInstance?.clearCache(true)
                                webViewInstance?.reload()
                                Toast.makeText(context, "Cache refreshed & reloaded", Toast.LENGTH_SHORT).show()
                            },
                        color = Color.Transparent,
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("Instant Fast Reload", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Revalidate caches and reload page resources", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Copy Page URL
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                showFloatingMenu = false
                                copyToClipboard(context, "Page URL", currentUrl)
                            },
                        color = Color.Transparent,
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(14.dp))
                            Text("Copy Page URL", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Open in External Browser
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                showFloatingMenu = false
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                        color = Color.Transparent,
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(14.dp))
                            Text("Open in External Browser", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Exit / Return to Sark Tools
                    if (onBack != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showFloatingMenu = false
                                    onBack()
                                },
                            color = Color.Transparent,
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.width(14.dp))
                                Text("Exit to App Dashboard", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    // JavaScript Alert Dialog
    if (jsAlertDialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                jsAlertCallback?.confirm()
                jsAlertDialogMessage = null
                jsAlertCallback = null
            },
            title = { Text(pageTitle) },
            text = { Text(jsAlertDialogMessage ?: "") },
            confirmButton = {
                TextButton(
                    onClick = {
                        jsAlertCallback?.confirm()
                        jsAlertDialogMessage = null
                        jsAlertCallback = null
                    },
                ) {
                    Text("OK")
                }
            },
        )
    }

    // JavaScript Confirm Dialog
    if (jsConfirmDialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                jsConfirmCallback?.cancel()
                jsConfirmDialogMessage = null
                jsConfirmCallback = null
            },
            title = { Text(pageTitle) },
            text = { Text(jsConfirmDialogMessage ?: "") },
            confirmButton = {
                TextButton(
                    onClick = {
                        jsConfirmCallback?.confirm()
                        jsConfirmDialogMessage = null
                        jsConfirmCallback = null
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        jsConfirmCallback?.cancel()
                        jsConfirmDialogMessage = null
                        jsConfirmCallback = null
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
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
