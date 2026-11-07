package com.toolbox.measurement.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BatteryInfoScreen() {
    val context = LocalContext.current
    var level by remember { mutableIntStateOf(0) }
    var scale by remember { mutableIntStateOf(100) }
    var status by remember { mutableStateOf("Unknown") }
    var health by remember { mutableStateOf("Good") }
    var plugged by remember { mutableStateOf("Unplugged") }
    var voltage by remember { mutableFloatStateOf(0f) }
    var temperature by remember { mutableFloatStateOf(0f) }
    var technology by remember { mutableStateOf("Li-ion") }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val rawScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    if (rawLevel >= 0 && rawScale > 0) {
                        level = rawLevel
                        scale = rawScale
                    }

                    status = when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                        BatteryManager.BATTERY_STATUS_FULL -> "Full"
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
                        else -> "Unknown"
                    }

                    health = when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
                        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                        else -> "Normal"
                    }

                    plugged = when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                        BatteryManager.BATTERY_PLUGGED_AC -> "AC Wall Charger"
                        BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
                        else -> "Battery Power"
                    }

                    val rawVolt = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    voltage = if (rawVolt > 1000) rawVolt / 1000f else rawVolt.toFloat()

                    val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                    temperature = rawTemp / 10f

                    technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val percentage = if (scale > 0) ((level.toFloat() / scale) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Battery hero gauge card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (status == "Charging") MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (status == "Charging") Icons.Default.BatteryChargingFull else Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = if (status == "Charging") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "$status • $plugged",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (percentage > 20) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        // Metrics grid
        Text(
            text = "HARDWARE DIAGNOSTICS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Health",
                value = health,
                icon = Icons.Default.BatteryAlert,
                tint = if (health == "Good" || health == "Normal") Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Temperature",
                value = String.format("%.1f °C", temperature),
                icon = Icons.Default.Thermostat,
                tint = if (temperature < 42f) Color(0xFF1976D2) else MaterialTheme.colorScheme.error,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Voltage",
                value = String.format("%.2f V", voltage),
                icon = Icons.Default.ElectricMeter,
                tint = Color(0xFFE65100),
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Chemistry",
                value = technology,
                icon = Icons.Default.Bolt,
                tint = Color(0xFF6A1B9A),
            )
        }

        // Battery Care Tip Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Battery Health Recommendation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "To prolong lithium-ion battery longevity, keep charge cycles between 20% and 80% and avoid charging in extreme temperatures exceeding 40°C.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tint.copy(alpha = 0.15f),
                modifier = Modifier.size(34.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
