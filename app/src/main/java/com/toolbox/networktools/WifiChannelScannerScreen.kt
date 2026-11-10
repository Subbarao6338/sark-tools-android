package com.toolbox.networktools

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WifiChannelScannerScreen() {
    var selectedBand by remember { mutableStateOf("2.4 GHz") }

    val channels24 = listOf(
        Triple(1, "2412 MHz", "Non-overlapping (Recommended)"),
        Triple(6, "2437 MHz", "Non-overlapping (Recommended)"),
        Triple(11, "2462 MHz", "Non-overlapping (Recommended)"),
        Triple(2, "2417 MHz", "High Overlap Risk"),
        Triple(3, "2422 MHz", "High Overlap Risk"),
        Triple(4, "2427 MHz", "High Overlap Risk")
    )

    val channels50 = listOf(
        Triple(36, "5180 MHz", "UNII-1 (Indoor Clean)"),
        Triple(40, "5200 MHz", "UNII-1 (Indoor Clean)"),
        Triple(44, "5220 MHz", "UNII-1 (Indoor Clean)"),
        Triple(48, "5240 MHz", "UNII-1 (Indoor Clean)"),
        Triple(149, "5745 MHz", "UNII-3 (High Power)"),
        Triple(153, "5765 MHz", "UNII-3 (High Power)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Wi-Fi Channel & Spectrum Advisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Inspect 2.4 GHz and 5 GHz frequency band allocations, interference zones, and optimal non-overlapping channels.")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = selectedBand == "2.4 GHz", onClick = { selectedBand = "2.4 GHz" }, label = { Text("2.4 GHz Band") })
            FilterChip(selected = selectedBand == "5 GHz", onClick = { selectedBand = "5 GHz" }, label = { Text("5 GHz Band") })
        }

        val list = if (selectedBand == "2.4 GHz") channels24 else channels50

        list.forEach { (ch, freq, status) ->
            val isGood = status.contains("Recommended") || status.contains("Clean") || status.contains("High Power")
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGood) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Channel $ch ($freq)", fontWeight = FontWeight.Bold)
                    Text(status, color = if (isGood) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
