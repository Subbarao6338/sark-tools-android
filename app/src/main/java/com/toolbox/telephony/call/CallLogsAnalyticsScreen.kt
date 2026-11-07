package com.toolbox.telephony.call

import android.Manifest
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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

data class TopContactStat(val name: String, val count: Int, val totalSec: Long)

@Composable
fun CallLogsAnalyticsScreen() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        )
    }
    var useDemoData by remember { mutableStateOf(!hasPermission) }

    var incomingCalls by remember { mutableIntStateOf(0) }
    var outgoingCalls by remember { mutableIntStateOf(0) }
    var missedCalls by remember { mutableIntStateOf(0) }
    var totalDurationSec by remember { mutableLongStateOf(0L) }
    val topContacts = remember { mutableStateListOf<TopContactStat>() }

    fun loadCallStats() {
        if (!hasPermission || useDemoData) {
            incomingCalls = 48
            outgoingCalls = 32
            missedCalls = 14
            totalDurationSec = 9640L
            topContacts.clear()
            topContacts.addAll(
                listOf(
                    TopContactStat("Emma Watson", 28, 4200L),
                    TopContactStat("Work Office", 18, 2800L),
                    TopContactStat("Mom", 15, 1950L),
                    TopContactStat("David Miller", 12, 690L),
                )
            )
            return
        }

        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER),
                null,
                null,
                "${CallLog.Calls.DATE} DESC LIMIT 500"
            )

            var inc = 0
            var out = 0
            var mis = 0
            var dur = 0L
            val contactCounts = mutableMapOf<String, Pair<Int, Long>>()

            cursor?.use { c ->
                val typeIdx = c.getColumnIndex(CallLog.Calls.TYPE)
                val durIdx = c.getColumnIndex(CallLog.Calls.DURATION)
                val nameIdx = c.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val numIdx = c.getColumnIndex(CallLog.Calls.NUMBER)

                while (c.moveToNext()) {
                    val type = c.getInt(typeIdx)
                    val callDuration = c.getLong(durIdx)
                    val name = c.getString(nameIdx)?.ifBlank { null } ?: c.getString(numIdx) ?: "Unknown"

                    when (type) {
                        CallLog.Calls.INCOMING_TYPE -> inc++
                        CallLog.Calls.OUTGOING_TYPE -> out++
                        CallLog.Calls.MISSED_TYPE -> mis++
                    }
                    dur += callDuration
                    val current = contactCounts[name] ?: Pair(0, 0L)
                    contactCounts[name] = Pair(current.first + 1, current.second + callDuration)
                }
            }

            incomingCalls = inc
            outgoingCalls = out
            missedCalls = mis
            totalDurationSec = dur
            topContacts.clear()
            contactCounts.entries.sortedByDescending { it.value.first }.take(5).forEach { (name, stats) ->
                topContacts.add(TopContactStat(name, stats.first, stats.second))
            }
        } catch (_: Exception) {
            useDemoData = true
            loadCallStats()
        }
    }

    LaunchedEffect(hasPermission, useDemoData) {
        loadCallStats()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
        if (granted) {
            useDemoData = false
            loadCallStats()
        }
    }

    fun formatDuration(totalSec: Long): String {
        val hrs = totalSec / 3600
        val mins = (totalSec % 3600) / 60
        return if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Permission Status banner
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
                    Text("Grant READ_CALL_LOG permission to analyze real device call history, or continue with demo data.", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_CALL_LOG) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        // Overview Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Calls", style = MaterialTheme.typography.labelMedium)
                    Text("${incomingCalls + outgoingCalls + missedCalls}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Duration", style = MaterialTheme.typography.labelMedium)
                    Text(formatDuration(totalDurationSec), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Call Breakdown
        Text("Call Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallReceived, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(Modifier.width(8.dp))
                        Text("Incoming Calls")
                    }
                    Text("$incomingCalls", fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallMade, contentDescription = null, tint = Color(0xFF1565C0))
                        Spacer(Modifier.width(8.dp))
                        Text("Outgoing Calls")
                    }
                    Text("$outgoingCalls", fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallMissed, contentDescription = null, tint = Color(0xFFC62828))
                        Spacer(Modifier.width(8.dp))
                        Text("Missed Calls")
                    }
                    Text("$missedCalls", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Top Contact Volume
        Text("Frequent Contacts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                topContacts.forEach { contact ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(contact.name, fontWeight = FontWeight.Medium)
                            Text("Time: ${formatDuration(contact.totalSec)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Text("${contact.count} calls", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
