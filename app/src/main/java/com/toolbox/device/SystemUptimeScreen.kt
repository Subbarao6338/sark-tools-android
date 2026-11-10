package com.toolbox.device

import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Composable
fun SystemUptimeScreen() {
    val uptimeMillis = SystemClock.uptimeMillis()
    val elapsedRealtimeMillis = SystemClock.elapsedRealtime()
    val bootTimeEpoch = System.currentTimeMillis() - elapsedRealtimeMillis

    fun formatDuration(ms: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(ms)
        val hours = TimeUnit.MILLISECONDS.toHours(ms) % 24
        val mins = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val secs = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return "${days}d ${hours}h ${mins}m ${secs}s"
    }

    val bootDateStr = Instant.ofEpochMilli(bootTimeEpoch)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"))

    val sleepMs = elapsedRealtimeMillis - uptimeMillis
    val deepSleepPct = if (elapsedRealtimeMillis > 0) (sleepMs.toDouble() / elapsedRealtimeMillis * 100) else 0.0

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
                Text("System Uptime & Sleep Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Inspect device kernel boot duration, active CPU awake cycles, and deep sleep metrics.")
            }
        }

        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Total Elapsed Time (Since Boot)", style = MaterialTheme.typography.labelMedium)
                Text(formatDuration(elapsedRealtimeMillis), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Device Boot Timestamp:", fontWeight = FontWeight.SemiBold)
                Text(bootDateStr)

                Text("CPU Awake Time (Non-Sleep):", fontWeight = FontWeight.SemiBold)
                Text(formatDuration(uptimeMillis))

                Text("Deep Sleep Duration:", fontWeight = FontWeight.SemiBold)
                Text(formatDuration(sleepMs))

                Text("Deep Sleep Ratio:", fontWeight = FontWeight.SemiBold)
                Text(String.format("%.1f%% of total power-on time", deepSleepPct), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
