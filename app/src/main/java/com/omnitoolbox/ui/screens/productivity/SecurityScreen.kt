package omni.toolbox.ui.screens.productivity

import androidx.core.content.edit
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import omni.toolbox.utils.PasswordStrengthAnalyzer

@Composable
fun SecurityScreen(navController: NavHostController, title: String = "Security Vault") {
    val isMainVault = title == "Security Vault" || title == "Privacy & Security Vault"
    val context = LocalContext.current

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isMainVault) {
                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf("Encryption", "Password Gen", "QR Studio")

                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> EncryptionTab()
                        1 -> PasswordTab()
                        2 -> QrStudioTab(navController)
                    }
                }
            } else {
                // Specialized sub-utility security view based on title
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (title) {
                        "App Locker", "app_locker" -> {
                            Text("Secure App Locker Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Select user or system apps to lock. Preferences are saved persistently in local storage.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))

                            val pm = context.packageManager
                            var searchQuery by remember { mutableStateOf("") }
                            var showSystemApps by remember { mutableStateOf(false) }
                            val prefs = remember { context.getSharedPreferences("app_locker_prefs", android.content.Context.MODE_PRIVATE) }

                            var appsList by remember { mutableStateOf<List<android.content.pm.ApplicationInfo>>(emptyList()) }
                            var isLoading by remember { mutableStateOf(true) }

                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                    val list = try {
                                        @Suppress("QueryPermissionsNeeded")
                                        pm.getInstalledApplications(PackageManager.GET_META_DATA)
                                            .sortedBy { it.loadLabel(pm).toString().lowercase() }
                                    } catch (e: Exception) {
                                        emptyList()
                                    }
                                    appsList = list
                                    isLoading = false
                                }
                            }

                            if (isLoading) {
                                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Show System Apps", style = MaterialTheme.typography.bodyMedium)
                                    Switch(checked = showSystemApps, onCheckedChange = { showSystemApps = it })
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    label = { Text("Search installed apps...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val filteredApps = remember(appsList, searchQuery, showSystemApps) {
                                    appsList.filter { app ->
                                        val isSystem = (app.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                                        val matchesSystem = showSystemApps || !isSystem
                                        val label = app.loadLabel(pm).toString()
                                        val matchesSearch = label.contains(searchQuery, ignoreCase = true) || app.packageName.contains(searchQuery, ignoreCase = true)
                                        matchesSystem && matchesSearch
                                    }
                                }

                                if (filteredApps.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                        Text("No applications found", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                                    }
                                } else {
                                    filteredApps.take(150).forEach { app -> // Take at most 150 to keep layout responsive
                                        AppLockerRow(app, pm, prefs)
                                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                    }
                                    if (filteredApps.size > 150) {
                                        Text(
                                            "Showing first 150 results. Please narrow down search.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                        "App Permissions", "app_permissions", "Permission Manager", "perm_manager" -> {
                            Text("Live Package Permission Monitor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Monitor and request permissions required by Omni Browser in real-time.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))

                            val pm = context.packageManager
                            val pkgInfo = remember {
                                try {
                                    pm.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            val declaredPermissions = pkgInfo?.requestedPermissions ?: emptyArray()

                            if (declaredPermissions.isEmpty()) {
                                Text("No permissions requested by this app package.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                            } else {
                                declaredPermissions.forEach { permission ->
                                    PermissionRow(permission, context)
                                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                }
                            }
                        }
                        else -> { // Privacy Check / Fallback
                            var scanning by remember { mutableStateOf(false) }
                            var auditResults by remember { mutableStateOf<List<Triple<String, String, Boolean>>?>(null) }
                            val coroutineScope = rememberCoroutineScope()

                            Text("Native Device Privacy & Security Audit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Inspect critical OS parameters and settings to find vulnerabilities or potential privacy leaks.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))

                            if (scanning) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Running real-time security audit...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                auditResults?.let { results ->
                                    Text("Audit Diagnostics:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    results.forEach { (name, value, isSafe) ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSafe) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isSafe) Color(0xFF1B5E20) else Color(0xFFB71C1C))
                                                    Text(value, style = MaterialTheme.typography.bodySmall, color = if (isSafe) Color(0xFF2E7D32) else Color(0xFFC62828))
                                                }
                                                Icon(
                                                    imageVector = if (isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = if (isSafe) Color(0xFF2E7D32) else Color(0xFFC62828)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                } ?: Text("Start scanning to audit your device settings, debugging interfaces, and cryptographic policies.", style = MaterialTheme.typography.bodyMedium)

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            scanning = true
                                            delay(1000)

                                            val resolver = context.contentResolver
                                            // Developer settings
                                            val devSettingsEnabled = android.provider.Settings.Global.getInt(resolver, android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0

                                            // ADB Enabled
                                            val adbEnabled = android.provider.Settings.Global.getInt(resolver, android.provider.Settings.Global.ADB_ENABLED, 0) != 0

                                            // Root / su binaries check
                                            val suPaths = listOf(
                                                "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
                                                "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
                                                "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"
                                            )
                                            val isRooted = suPaths.any { java.io.File(it).exists() }

                                            // Network cleartext policy
                                            val cleartextPermitted = android.security.NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted

                                            // GPS location provider status
                                            val locManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
                                            val isGpsActive = locManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)

                                            auditResults = listOf(
                                                Triple(
                                                    "Developer Mode",
                                                    if (devSettingsEnabled) "Vulnerable: Developer Settings are ENABLED" else "Safe: Developer Settings are disabled",
                                                    !devSettingsEnabled
                                                ),
                                                Triple(
                                                    "ADB/USB Debugging",
                                                    if (adbEnabled) "Vulnerable: ADB Debugging is ENABLED" else "Safe: ADB Debugging is disabled",
                                                    !adbEnabled
                                                ),
                                                Triple(
                                                    "Root su Binaries",
                                                    if (isRooted) "Vulnerable: Root / su binary files detected!" else "Safe: Device is not rooted",
                                                    !isRooted
                                                ),
                                                Triple(
                                                    "Cleartext Network Policy",
                                                    if (cleartextPermitted) "Warning: Cleartext (HTTP) traffic allowed" else "Secure: HTTPS only mode enforced",
                                                    !cleartextPermitted
                                                ),
                                                Triple(
                                                    "GPS Location Access",
                                                    if (isGpsActive) "Info: GPS location services are active" else "Info: GPS location is disabled",
                                                    true
                                                )
                                            )
                                            scanning = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Run Native Privacy Audit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EncryptionTab() {
    var textToEncrypt by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("AES-256 Symmetric Block Cipher", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = textToEncrypt,
            onValueChange = { textToEncrypt = it },
            label = { Text("Plaintext / Ciphertext") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = secretKey,
            onValueChange = { secretKey = it },
            label = { Text("Secret Key (Salt Phrase)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { resultText = encrypt(textToEncrypt, secretKey) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Encrypt")
            }
            Button(
                onClick = { resultText = decrypt(textToEncrypt, secretKey) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Decrypt")
            }
        }

        if (resultText.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Result Output", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(resultText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun PasswordTab() {
    var length by remember { mutableFloatStateOf(16f) }
    var includeSymbols by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }
    var customPasswordInput by remember { mutableStateOf("") }
    var useManualInput by remember { mutableStateOf(false) }

    val activePassword = if (useManualInput) customPasswordInput else generatedPassword

    // Analyze password in real-time
    val analysisResult = remember(activePassword) {
        PasswordStrengthAnalyzer.analyze(activePassword)
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("High-Entropy Password Generator & Analyzer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Segmented control to choose between generate or evaluate manual input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { useManualInput = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!useManualInput) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (!useManualInput) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Generate Password")
            }
            Button(
                onClick = { useManualInput = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (useManualInput) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (useManualInput) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Evaluate Own")
            }
        }

        if (useManualInput) {
            OutlinedTextField(
                value = customPasswordInput,
                onValueChange = { customPasswordInput = it },
                label = { Text("Enter your password to analyze") },
                placeholder = { Text("Type here...") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (customPasswordInput.isNotEmpty()) {
                        IconButton(onClick = { customPasswordInput = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Text(
                        generatedPassword.ifEmpty { "P@ssword123!" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column {
                Text("Password Length: ${length.toInt()}")
                Slider(value = length, onValueChange = { length = it }, valueRange = 8f..64f)
            }

            ListItem(
                headlineContent = { Text("Include Symbols") },
                trailingContent = { Switch(checked = includeSymbols, onCheckedChange = { includeSymbols = it }) }
            )
            ListItem(
                headlineContent = { Text("Include Numbers") },
                trailingContent = { Switch(checked = includeNumbers, onCheckedChange = { includeNumbers = it }) }
            )

            Button(
                onClick = { generatedPassword = generatePassword(length.toInt(), includeSymbols, includeNumbers) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate New Password")
            }
        }

        // Live Cryptographic Evaluation Area
        if (activePassword.isNotEmpty()) {
            val (gradeColor, gradeLabel) = when (analysisResult.strengthGrade) {
                PasswordStrengthAnalyzer.StrengthGrade.VERY_WEAK -> Color(0xFFD32F2F) to "VERY WEAK"
                PasswordStrengthAnalyzer.StrengthGrade.WEAK -> Color(0xFFF57C00) to "WEAK"
                PasswordStrengthAnalyzer.StrengthGrade.MEDIUM -> Color(0xFFFBC02D) to "MEDIUM"
                PasswordStrengthAnalyzer.StrengthGrade.STRONG -> Color(0xFF388E3C) to "STRONG"
                PasswordStrengthAnalyzer.StrengthGrade.VERY_STRONG -> Color(0xFF1B5E20) to "VERY STRONG"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Cryptographic Analysis",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = gradeColor,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = gradeLabel,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Entropy Meters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Shannon Entropy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Text(
                                String.format(Locale.getDefault(), "%.2f bits", analysisResult.entropy),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pool Entropy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            val poolEntropyValue = PasswordStrengthAnalyzer.calculatePoolEntropy(activePassword)
                            Text(
                                String.format(Locale.getDefault(), "%.1f bits", poolEntropyValue),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Bulleted Cryptographic Feedback list
                    if (analysisResult.feedback.isNotEmpty()) {
                        Text(
                            "Evaluation Checklist:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            analysisResult.feedback.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("•", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 8.dp))
                                    Text(item, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QrStudioTab(navController: NavHostController) {
    var qrText by remember { mutableStateOf("Omni Toolbox Secure QR") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun generateQr(content: String) {
        if (content.isBlank()) {
            qrBitmap = null
            return
        }
        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap[x, y] = if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                }
            }
            qrBitmap = bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(qrText) {
        generateQr(qrText)
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Secure QR Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = qrText,
            onValueChange = { qrText = it },
            label = { Text("Data for QR Matrix") },
            modifier = Modifier.fillMaxWidth()
        )

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            qrBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp)
                )
            } ?: Box(modifier = Modifier.size(200.dp).background(Color.LightGray))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.navigate("qr_scanner") }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan")
            }
            Button(onClick = { /* Export */ }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save QR")
            }
        }
    }
}

@Composable
fun AppLockerRow(app: ApplicationInfo, pm: PackageManager, prefs: android.content.SharedPreferences) {
    var isLocked by remember(app.packageName) {
        mutableStateOf(prefs.getBoolean("app_locked_${app.packageName}", false))
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(app.loadLabel(pm).toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
        Switch(
            checked = isLocked,
            onCheckedChange = { checked ->
                isLocked = checked
                prefs.edit { putBoolean("app_locked_${app.packageName}", checked) }
            }
        )
    }
}

@Composable
fun PermissionRow(permission: String, context: Context) {
    val shortName = permission.substringAfterLast(".")
    var isGranted by remember(permission) {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        isGranted = granted
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(shortName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                if (isGranted) "Status: GRANTED" else "Status: DENIED",
                style = MaterialTheme.typography.bodySmall,
                color = if (isGranted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
            )
        }

        if (isGranted) {
            OutlinedButton(
                onClick = {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.outline)
            ) {
                Text("Revoke (Settings)")
            }
        } else {
            Button(
                onClick = { launcher.launch(permission) }
            ) {
                Text("Request")
            }
        }
    }
}

// Logic Utils
fun encrypt(strToEncrypt: String, key: String): String {
    return try {
        if (key.isEmpty()) return "Error: Key cannot be empty"

        // PBKDF2 Key Derivation
        val salt = ByteArray(16)
        java.security.SecureRandom().nextBytes(salt)
        val iterations = 1000
        val keyLength = 256
        val secretKeyFactory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keySpec = javax.crypto.spec.PBEKeySpec(key.toCharArray(), salt, iterations, keyLength)
        val keyBytes = secretKeyFactory.generateSecret(keySpec).encoded
        val secretKey = SecretKeySpec(keyBytes, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16)
        java.security.SecureRandom().nextBytes(iv)
        val ivSpec = javax.crypto.spec.IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encrypted = cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8))

        // Output format: salt (16 bytes) + iv (16 bytes) + encrypted data
        val combined = salt + iv + encrypted
        Base64.encodeToString(combined, Base64.DEFAULT)
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

fun decrypt(strToDecrypt: String, key: String): String {
    return try {
        if (key.isEmpty()) return "Error: Key cannot be empty"
        val combined = Base64.decode(strToDecrypt, Base64.DEFAULT)
        if (combined.size < 32) return "Error: Invalid Payload (too short)"

        val salt = combined.sliceArray(0 until 16)
        val iv = combined.sliceArray(16 until 32)
        val encrypted = combined.sliceArray(32 until combined.size)

        // PBKDF2 Key Derivation using extracted salt
        val iterations = 1000
        val keyLength = 256
        val secretKeyFactory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keySpec = javax.crypto.spec.PBEKeySpec(key.toCharArray(), salt, iterations, keyLength)
        val keyBytes = secretKeyFactory.generateSecret(keySpec).encoded
        val secretKey = SecretKeySpec(keyBytes, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = javax.crypto.spec.IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        String(cipher.doFinal(encrypted), Charsets.UTF_8)
    } catch (e: Exception) {
        "Error: Invalid Key or Payload"
    }
}

fun generatePassword(length: Int, symbols: Boolean, numbers: Boolean): String {
    val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                   (if (numbers) "0123456789" else "") +
                   (if (symbols) "!@#$%^&*()_+-=[]{}|;:,.<>?" else "")
    return (1..length)
        .map { Random().nextInt(charPool.length) }
        .map(charPool::get)
        .joinToString("")
}
