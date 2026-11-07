package com.toolbox.datetime.moonphase

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
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.cos
import kotlin.math.roundToInt

data class MoonPhaseInfo(
    val phaseName: String,
    val illuminationPercent: Int,
    val ageDays: Double,
    val daysToNextFullMoon: Int,
    val daysToNextNewMoon: Int,
)

// Synodic month is 29.53058770576 days
// Known New Moon: Jan 11, 2024
fun calculateMoonPhase(date: LocalDate = LocalDate.now()): MoonPhaseInfo {
    val knownNewMoon = LocalDate.of(2024, 1, 11)
    val daysSince = ChronoUnit.DAYS.between(knownNewMoon, date)
    val synodicMonth = 29.53058867
    val phaseProgress = (daysSince % synodicMonth + synodicMonth) % synodicMonth
    val illumination = ((1.0 - cos(2.0 * Math.PI * phaseProgress / synodicMonth)) / 2.0 * 100.0).roundToInt()

    val phaseName = when {
        phaseProgress < 1.0 || phaseProgress >= 28.5 -> "New Moon"
        phaseProgress < 6.5 -> "Waxing Crescent"
        phaseProgress < 8.5 -> "First Quarter"
        phaseProgress < 13.5 -> "Waxing Gibbous"
        phaseProgress < 16.0 -> "Full Moon"
        phaseProgress < 21.0 -> "Waning Gibbous"
        phaseProgress < 23.5 -> "Last Quarter"
        else -> "Waning Crescent"
    }

    val daysToFull = ((14.765 - phaseProgress + synodicMonth) % synodicMonth).roundToInt()
    val daysToNew = ((synodicMonth - phaseProgress) % synodicMonth).roundToInt()

    return MoonPhaseInfo(
        phaseName = phaseName,
        illuminationPercent = illumination,
        ageDays = (phaseProgress * 10.0).roundToInt() / 10.0,
        daysToNextFullMoon = daysToFull,
        daysToNextNewMoon = daysToNew,
    )
}

@Composable
fun MoonPhaseScreen() {
    val moonInfo = remember { calculateMoonPhase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Moon Hero Display
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier.fillMaxWidth(),
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
                        .background(Color(0xFF263238)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = Color(0xFFFFD54F),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = moonInfo.phaseName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${moonInfo.illuminationPercent}% Illuminated • Age ${moonInfo.ageDays} days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Details Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.weight(1f),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Next Full Moon", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("in ${moonInfo.daysToNextFullMoon} days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.weight(1f),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Next New Moon", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("in ${moonInfo.daysToNextNewMoon} days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Sun Times Estimate
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFF57F17))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Solar Day Standard Reference", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("Average Sunrise", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("06:05 AM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Solar Noon", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("12:15 PM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Average Sunset", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("06:25 PM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
