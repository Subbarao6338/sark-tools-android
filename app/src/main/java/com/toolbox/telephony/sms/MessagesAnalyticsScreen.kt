package com.toolbox.telephony.sms

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Telephony
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

data class TopSenderStat(val address: String, val count: Int)

@Composable
fun MessagesAnalyticsScreen() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }
    var useDemoData by remember { mutableStateOf(!hasPermission) }

    var inboxCount by remember { mutableIntStateOf(0) }
    var sentCount by remember { mutableIntStateOf(0) }
    val topSenders = remember { mutableStateListOf<TopSenderStat>() }

    fun loadSmsStats() {
        if (!hasPermission || useDemoData) {
            inboxCount = 142
            sentCount = 98
            topSenders.clear()
            topSenders.addAll(
                listOf(
                    TopSenderStat("Bank Alerts / OTP", 54),
                    TopSenderStat("Alice", 41),
                    TopSenderStat("Delivery Updates", 26),
                    TopSenderStat("Bob", 19),
                )
            )
            return
        }

        try {
            val cursor = context.contentResolver.query(
                Uri.parse("content://sms"),
                arrayOf("type", "address"),
                null,
                null,
                "date DESC LIMIT 500"
            )

            var inb = 0
            var snt = 0
            val addressMap = mutableMapOf<String, Int>()

            cursor?.use { c ->
                val typeIdx = c.getColumnIndex("type")
                val addrIdx = c.getColumnIndex("address")

                while (c.moveToNext()) {
                    val type = c.getInt(typeIdx)
                    val addr = c.getString(addrIdx) ?: "Unknown"

                    if (type == Telephony.Sms.MESSAGE_TYPE_INBOX) {
                        inb++
                    } else if (type == Telephony.Sms.MESSAGE_TYPE_SENT) {
                        snt++
                    }
                    addressMap[addr] = (addressMap[addr] ?: 0) + 1
                }
            }

            inboxCount = inb
            sentCount = snt
            topSenders.clear()
            addressMap.entries.sortedByDescending { it.value }.take(5).forEach { (addr, cnt) ->
                topSenders.add(TopSenderStat(addr, cnt))
            }
        } catch (_: Exception) {
            useDemoData = true
            loadSmsStats()
        }
    }

    LaunchedEffect(hasPermission, useDemoData) {
        loadSmsStats()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
        if (granted) {
            useDemoData = false
            loadSmsStats()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (!hasPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(Modifier.width(8.dp))
                        Text("Viewing Demo Analytics", fontWeight = FontWeight.Bold)
                    }
                    Text("Grant READ_SMS permission to analyze local messages volume, or continue exploring with demo data.", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_SMS) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        // Summary Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Messages", style = MaterialTheme.typography.labelMedium)
                    Text("${inboxCount + sentCount}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Received / Sent", style = MaterialTheme.typography.labelMedium)
                    Text("$inboxCount / $sentCount", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Ratio Breakdown
        Text("Traffic Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = Color(0xFF1976D2))
                        Spacer(Modifier.width(8.dp))
                        Text("Inbox / Received")
                    }
                    Text("$inboxCount", fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Outbox, contentDescription = null, tint = Color(0xFF388E3C))
                        Spacer(Modifier.width(8.dp))
                        Text("Sent Messages")
                    }
                    Text("$sentCount", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Top Senders & Threads
        Text("Top Conversationalists / Senders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                topSenders.forEach { sender ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(sender.address, fontWeight = FontWeight.Medium)
                        Text("${sender.count} msgs", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
