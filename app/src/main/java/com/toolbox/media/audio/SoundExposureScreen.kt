package com.toolbox.media.audio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SoundExposureScreen() {
    var dbLevel by remember { mutableStateOf(75f) }

    val nioshMaxHours = when {
        dbLevel < 85 -> "Unlimited (Safe)"
        dbLevel <= 88 -> "4 hours"
        dbLevel <= 91 -> "2 hours"
        dbLevel <= 94 -> "1 hour"
        dbLevel <= 97 -> "30 minutes"
        dbLevel <= 100 -> "15 minutes"
        dbLevel <= 106 -> "< 3.7 minutes"
        dbLevel <= 112 -> "< 1 minute"
        else -> "Immediate risk of hearing loss"
    }

    val (riskLevel, riskColor) = when {
        dbLevel < 70 -> "Safe / Quiet" to Color(0xFF2E7D32)
        dbLevel < 85 -> "Moderate / Safe Exposure" to Color(0xFF388E3C)
        dbLevel < 95 -> "Hazardous (Hearing protection recommended)" to Color(0xFFFFA000)
        dbLevel < 110 -> "Dangerous (Strict time limits apply)" to Color(0xFFE64A19)
        else -> "Extreme Hazard (Risk of acute acoustic trauma)" to Color(0xFFD32F2F)
    }

    val environmentalExamples = when {
        dbLevel < 30 -> "Whisper, quiet library, rustling leaves"
        dbLevel < 50 -> "Quiet room, quiet residential area, refrigerator hum"
        dbLevel < 70 -> "Normal conversational speech, air conditioner"
        dbLevel < 85 -> "City traffic inside car, vacuum cleaner, noisy restaurant"
        dbLevel < 95 -> "Lawn mower, heavy truck passing, blender"
        dbLevel < 110 -> "Motorcycle, jackhammer, sports stadium"
        else -> "Siren nearby, rock concert, jet engine takeoff"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = riskColor.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = null,
                    tint = riskColor,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${dbLevel.toInt()} dB",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = riskColor
                )
                Text(
                    text = riskLevel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = riskColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "NIOSH Max Safe Exposure: $nioshMaxHours",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text("Sound Intensity Slider (${dbLevel.toInt()} dB)", fontWeight = FontWeight.SemiBold)
        Slider(
            value = dbLevel,
            onValueChange = { dbLevel = it },
            valueRange = 20f..130f,
            steps = 110,
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Typical Sound Example", fontWeight = FontWeight.Bold)
                Text(environmentalExamples, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("WHO / OSHA Workplace Guidelines", fontWeight = FontWeight.Bold)
                Text("• 85 dB: 8-hour maximum daily exposure without protection", style = MaterialTheme.typography.bodySmall)
                Text("• 88 dB: 4-hour limit (intensity doubles every 3 dB)", style = MaterialTheme.typography.bodySmall)
                Text("• 91 dB: 2-hour limit", style = MaterialTheme.typography.bodySmall)
                Text("• 100 dB: 15-minute maximum limit", style = MaterialTheme.typography.bodySmall)
                Text("• 120 dB+: Pain threshold; causes immediate structural harm", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
