package omni.toolbox.ui.screens.developer

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun getMinSdkVersionSafe(appInfo: ApplicationInfo): Int {
    return try {
        appInfo.minSdkVersion
    } catch (e: Throwable) {
        26
    }
}

fun getTargetSdkVersionSafe(appInfo: ApplicationInfo): Int {
    return try {
        appInfo.targetSdkVersion
    } catch (e: Throwable) {
        35
    }
}

fun generateManifestXml(context: Context, appInfo: ApplicationInfo): String {
    val pm = context.packageManager
    return try {
        val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(
                appInfo.packageName,
                PackageManager.PackageInfoFlags.of(
                    (PackageManager.GET_ACTIVITIES or
                     PackageManager.GET_SERVICES or
                     PackageManager.GET_RECEIVERS or
                     PackageManager.GET_PROVIDERS or
                     PackageManager.GET_PERMISSIONS).toLong()
                )
            )
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(
                appInfo.packageName,
                PackageManager.GET_ACTIVITIES or
                PackageManager.GET_SERVICES or
                PackageManager.GET_RECEIVERS or
                PackageManager.GET_PROVIDERS or
                PackageManager.GET_PERMISSIONS
            )
        }

        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        sb.append("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n")
        sb.append("    package=\"${packageInfo.packageName}\"\n")
        @Suppress("DEPRECATION")
        sb.append("    android:versionCode=\"${packageInfo.versionCode}\"\n")
        sb.append("    android:versionName=\"${packageInfo.versionName ?: "1.0"}\">\n\n")

        // Permissions
        val permissions = packageInfo.requestedPermissions
        if (!permissions.isNullOrEmpty()) {
            permissions.forEach { perm ->
                sb.append("    <uses-permission android:name=\"$perm\" />\n")
            }
            sb.append("\n")
        }

        sb.append("    <application\n")
        sb.append("        android:label=\"${appInfo.loadLabel(pm).toString().replace("\"", "\\\"")}\"\n")
        sb.append("        android:targetSdkVersion=\"${getTargetSdkVersionSafe(appInfo)}\"\n")
        sb.append("        android:minSdkVersion=\"${getMinSdkVersionSafe(appInfo)}\"\n")
        sb.append("        android:uid=\"${appInfo.uid}\">\n\n")

        // Activities
        val activities = packageInfo.activities
        if (!activities.isNullOrEmpty()) {
            activities.forEach { act ->
                sb.append("        <activity\n")
                sb.append("            android:name=\"${act.name}\"\n")
                sb.append("            android:exported=\"${act.exported}\" />\n")
            }
            sb.append("\n")
        }

        // Services
        val services = packageInfo.services
        if (!services.isNullOrEmpty()) {
            services.forEach { srv ->
                sb.append("        <service\n")
                sb.append("            android:name=\"${srv.name}\"\n")
                sb.append("            android:exported=\"${srv.exported}\" />\n")
            }
            sb.append("\n")
        }

        // Receivers
        val receivers = packageInfo.receivers
        if (!receivers.isNullOrEmpty()) {
            receivers.forEach { rec ->
                sb.append("        <receiver\n")
                sb.append("            android:name=\"${rec.name}\"\n")
                sb.append("            android:exported=\"${rec.exported}\" />\n")
            }
            sb.append("\n")
        }

        // Providers
        val providers = packageInfo.providers
        if (!providers.isNullOrEmpty()) {
            providers.forEach { prov ->
                sb.append("        <provider\n")
                sb.append("            android:name=\"${prov.name}\"\n")
                sb.append("            android:authorities=\"${prov.authority}\"\n")
                sb.append("            android:exported=\"${prov.exported}\" />\n")
            }
        }

        sb.append("    </application>\n")
        sb.append("</manifest>")
        sb.toString()
    } catch (e: Exception) {
        "<!-- Error decompile via Package Manager: ${e.localizedMessage} -->\n" +
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
        "    package=\"${appInfo.packageName}\">\n" +
        "    <application android:label=\"${appInfo.loadLabel(pm)}\" />\n" +
        "</manifest>"
    }
}

enum class AppSortOption(val displayName: String) {
    ALPHABETICAL("A-Z"),
    TARGET_SDK("Target SDK"),
    UID("UID"),
    PACKAGE_NAME("Package")
}

@Composable
fun AppDecompilerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val pm = context.packageManager

    // Loaded list
    val installedApps = remember {
        @Suppress("QueryPermissionsNeeded")
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .sortedBy { it.loadLabel(pm).toString().lowercase() }
    }

    var selectedApp by remember { mutableStateOf<ApplicationInfo?>(null) }
    var manifestContent by remember { mutableStateOf<String?>(null) }
    var showManifest by remember { mutableStateOf(false) }

    // Search and filters
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf(AppSortOption.ALPHABETICAL) }
    var filterSystemApps by remember { mutableStateOf(true) } // Show system apps
    var filterUserApps by remember { mutableStateOf(true) } // Show user apps

    // Cache metrics state
    var cacheSizeStr by remember { mutableStateOf("0 B") }
    var extractedApkFiles by remember { mutableStateOf<List<File>>(emptyList()) }

    val scope = rememberCoroutineScope()

    fun refreshCacheInfo() {
        val cacheDir = File(context.cacheDir, "extracted_apks")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val files = cacheDir.listFiles()?.filter { it.extension == "apk" } ?: emptyList()
        extractedApkFiles = files
        val totalBytes = files.sumOf { it.length() }
        cacheSizeStr = when {
            totalBytes >= 1024 * 1024 -> String.format(java.util.Locale.getDefault(), "%.2f MB", totalBytes.toDouble() / (1024 * 1024))
            totalBytes >= 1024 -> String.format(java.util.Locale.getDefault(), "%.2f KB", totalBytes.toDouble() / 1024)
            else -> "$totalBytes B"
        }
    }

    LaunchedEffect(Unit) {
        refreshCacheInfo()
    }

    // Filtered lists
    val processedApps = remember(installedApps, searchQuery, sortOption, filterSystemApps, filterUserApps) {
        installedApps.asSequence()
            .filter { app ->
                val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                if (isSystem && !filterSystemApps) return@filter false
                if (!isSystem && !filterUserApps) return@filter false

                val label = app.loadLabel(pm).toString()
                label.contains(searchQuery, ignoreCase = true) || app.packageName.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith { a, b ->
                when (sortOption) {
                    AppSortOption.ALPHABETICAL -> {
                        a.loadLabel(pm).toString().compareTo(b.loadLabel(pm).toString(), ignoreCase = true)
                    }
                    AppSortOption.TARGET_SDK -> {
                        getTargetSdkVersionSafe(b).compareTo(getTargetSdkVersionSafe(a)) // descending
                    }
                    AppSortOption.UID -> {
                        a.uid.compareTo(b.uid)
                    }
                    AppSortOption.PACKAGE_NAME -> {
                        a.packageName.compareTo(b.packageName, ignoreCase = true)
                    }
                }
            }
            .toList()
    }

    ToolScreen(
        title = "App Inspector & Decompiler",
        onBack = {
            if (showManifest) {
                showManifest = false
            } else if (selectedApp != null) {
                selectedApp = null
            } else {
                navController.popBackStack()
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showManifest && selectedApp != null) {
                ManifestViewer(selectedApp!!, manifestContent ?: "Loading manifest...")
            } else if (selectedApp != null) {
                AppDetailView(
                    app = selectedApp!!,
                    pm = pm,
                    onDecompile = {
                        manifestContent = generateManifestXml(context, selectedApp!!)
                        showManifest = true
                    },
                    onExtract = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val sourceFile = File(selectedApp!!.sourceDir)
                                val cacheDir = File(context.cacheDir, "extracted_apks")
                                if (!cacheDir.exists()) {
                                    cacheDir.mkdirs()
                                }
                                val destFile = File(cacheDir, "${selectedApp!!.packageName}.apk")
                                FileInputStream(sourceFile).use { input ->
                                    FileOutputStream(destFile).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "APK extracted successfully to Cache!", Toast.LENGTH_LONG).show()
                                    refreshCacheInfo()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Search Bar and Filters Box
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Search field
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Search apps or package names") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sort Option selector & Category filters
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Sort by:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    AppSortOption.values().forEach { option ->
                                        FilterChip(
                                            selected = sortOption == option,
                                            onClick = { sortOption = option },
                                            label = { Text(option.displayName, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = filterUserApps,
                                        onCheckedChange = { filterUserApps = it }
                                    )
                                    Text("User Apps", style = MaterialTheme.typography.bodySmall)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = filterSystemApps,
                                        onCheckedChange = { filterSystemApps = it }
                                    )
                                    Text("System Apps", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Extraction Cache Status Bar
                    if (extractedApkFiles.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Extraction Cache", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Text("${extractedApkFiles.size} APKs, total $cacheSizeStr", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(
                                    onClick = {
                                        val cacheDir = File(context.cacheDir, "extracted_apks")
                                        if (cacheDir.exists()) {
                                            cacheDir.deleteRecursively()
                                        }
                                        refreshCacheInfo()
                                        Toast.makeText(context, "Extraction cache cleared", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear Cache", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // List of Apps
                    if (processedApps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No applications match your criteria", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(processedApps) { app ->
                                val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            app.loadLabel(pm).toString(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    supportingContent = {
                                        Column {
                                            Text(app.packageName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text("SDK: ${getTargetSdkVersionSafe(app)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                                if (isSystem) {
                                                    Text("System", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                                } else {
                                                    Text("User", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.Android,
                                            contentDescription = null,
                                            tint = if (isSystem) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedApp = app },
                                    trailingContent = {
                                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                                    }
                                )
                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppDetailView(app: ApplicationInfo, pm: PackageManager, onDecompile: () -> Unit, onExtract: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Android, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(app.loadLabel(pm).toString(), style = MaterialTheme.typography.headlineSmall)
                Text(app.packageName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("App Details", style = MaterialTheme.typography.titleMedium)
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Target SDK", getTargetSdkVersionSafe(app).toString())
                DetailRow("Min SDK", getMinSdkVersionSafe(app).toString())
                DetailRow("UID", app.uid.toString())
                DetailRow("Data Dir", app.dataDir ?: "N/A")
                DetailRow("Source Dir", app.sourceDir)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onDecompile, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Code, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View AndroidManifest.xml")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onExtract, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Extract APK to Cache")
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ManifestViewer(app: ApplicationInfo, content: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AndroidManifest.xml", style = MaterialTheme.typography.titleMedium)
        Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxSize(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                item {
                    Text(
                        text = content,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
