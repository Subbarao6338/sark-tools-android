package com.toolbox.web

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL

@Composable
fun WebToolsScreen() {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("URL Inspector", "HTTP Lookup", "DNS / Ping", "User Agent")

    var inputUrl by remember { mutableStateOf("https://example.com") }
    var inspectedProtocol by remember { mutableStateOf("") }
    var inspectedHost by remember { mutableStateOf("") }
    var inspectedPort by remember { mutableStateOf("") }
    var inspectedPath by remember { mutableStateOf("") }
    var inspectedQuery by remember { mutableStateOf("") }

    // HTTP Lookup State
    var httpStatusResult by remember { mutableStateOf("") }
    var httpHeadersResult by remember { mutableStateOf("") }
    var isCheckingHttp by remember { mutableStateOf(false) }

    // DNS / Ping State
    var pingHost by remember { mutableStateOf("google.com") }
    var pingResult by remember { mutableStateOf("") }
    var isPinging by remember { mutableStateOf(false) }

    // User Agent
    val systemUserAgent = remember { System.getProperty("http.agent") ?: "Android Dalvik/2.1.0" }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (selectedTab) {
                0 -> { // URL Inspector
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        label = { Text("Enter Web URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            try {
                                val parsed = URL(if (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://")) "https://$inputUrl" else inputUrl)
                                inspectedProtocol = parsed.protocol.uppercase()
                                inspectedHost = parsed.host
                                inspectedPort = if (parsed.port != -1) parsed.port.toString() else (if (parsed.protocol == "https") "443" else "80")
                                inspectedPath = parsed.path.ifEmpty { "/" }
                                inspectedQuery = parsed.query ?: "None"
                            } catch (e: Exception) {
                                inspectedProtocol = "Invalid URL"
                                inspectedHost = e.message ?: "Error"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Parse & Inspect URL")
                    }

                    if (inspectedHost.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Protocol: $inspectedProtocol", fontWeight = FontWeight.Bold)
                                Text("Host: $inspectedHost")
                                Text("Port: $inspectedPort")
                                Text("Path: $inspectedPath")
                                Text("Query String: $inspectedQuery")
                            }
                        }
                    }
                }
                1 -> { // HTTP Status Lookup
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        label = { Text("Target Endpoint URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                isCheckingHttp = true
                                try {
                                    val formatted = if (!inputUrl.startsWith("http")) "https://$inputUrl" else inputUrl
                                    val connection = URL(formatted).openConnection() as HttpURLConnection
                                    connection.requestMethod = "HEAD"
                                    connection.connectTimeout = 5000
                                    connection.readTimeout = 5000
                                    val code = connection.responseCode
                                    val msg = connection.responseMessage
                                    val headers = connection.headerFields.entries.joinToString("\n") { (k, v) ->
                                        "${k ?: "Status"}: ${v.joinToString(",")}"
                                    }
                                    withContext(Dispatchers.Main) {
                                        httpStatusResult = "$code $msg"
                                        httpHeadersResult = headers
                                        isCheckingHttp = false
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        httpStatusResult = "Offline / Connection Error"
                                        httpHeadersResult = e.localizedMessage ?: "Failed to connect"
                                        isCheckingHttp = false
                                    }
                                }
                            }
                        },
                        enabled = !isCheckingHttp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Http, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isCheckingHttp) "Requesting..." else "Send HEAD Request")
                    }

                    if (httpStatusResult.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Response: $httpStatusResult", fontWeight = FontWeight.Bold)
                                Text("Response Headers:", fontWeight = FontWeight.SemiBold)
                                Text(
                                    httpHeadersResult,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
                2 -> { // DNS / Ping
                    OutlinedTextField(
                        value = pingHost,
                        onValueChange = { pingHost = it },
                        label = { Text("Hostname or IP") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                isPinging = true
                                try {
                                    val start = System.currentTimeMillis()
                                    val addr = InetAddress.getByName(pingHost)
                                    val resolvedIp = addr.hostAddress
                                    val reachable = addr.isReachable(3000)
                                    val elapsed = System.currentTimeMillis() - start
                                    withContext(Dispatchers.Main) {
                                        pingResult = "Resolved IP: $resolvedIp\nReachable: $reachable\nRound-trip latency: ${elapsed}ms"
                                        isPinging = false
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        pingResult = "DNS Resolution Failed: ${e.message}"
                                        isPinging = false
                                    }
                                }
                            }
                        },
                        enabled = !isPinging,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Dns, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isPinging) "Resolving..." else "Resolve DNS & Ping")
                    }

                    if (pingResult.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(pingResult, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
                3 -> { // User Agent
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Current Device User-Agent", fontWeight = FontWeight.Bold)
                            Text(systemUserAgent, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
