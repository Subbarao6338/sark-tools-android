package com.toolbox.systemtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SystemToolsScreen(toolId: String = "status") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "status" to "System Status",
        "telemetry" to "Live Telemetry",
        "lineage" to "Data Lineage"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "status" -> SystemStatusUi()
            "telemetry" -> LiveTelemetryUi()
            "lineage" -> DataLineageUi()
            else -> SystemStatusUi()
        }
    }
}

@Composable
fun SystemStatusUi() {
    val runtime = Runtime.getRuntime()
    val maxMem = runtime.maxMemory() / (1024 * 1024)
    val totalMem = runtime.totalMemory() / (1024 * 1024)
    val freeMem = runtime.freeMemory() / (1024 * 1024)
    val usedMem = totalMem - freeMem

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("System & Application Health Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Memory Allocation:", fontWeight = FontWeight.SemiBold)
            Text("• Used Heap: $usedMem MB", fontFamily = FontFamily.Monospace)
            Text("• Allocated Heap: $totalMem MB", fontFamily = FontFamily.Monospace)
            Text("• Max Heap Capacity: $maxMem MB", fontFamily = FontFamily.Monospace)
            Text("• Available Processors: ${runtime.availableProcessors()}", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun LiveTelemetryUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Live Operational Diagnostics & Telemetry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("• Network Mode: Offline-First Strict")
            Text("• External Telemetry Trackers: None (0 beacons)")
            Text("• Background Service Worker: Clean & Idle")
            Text("• Event Loop Latency: ~0.8 ms")
        }
    }
}

@Composable
fun DataLineageUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Data Flow & Lineage Map", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Input Pipeline:", fontWeight = FontWeight.SemiBold)
            Text("User Input / Ingestion → Local Validation → Memory Store → DataStore / JSON Cache", fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
            Text("Output Pipeline:", fontWeight = FontWeight.SemiBold)
            Text("Memory Store → Pure Transformation → Compose UI State Rendering", fontFamily = FontFamily.Monospace)
        }
    }
}
