package com.toolbox.everyday.interval

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class IntervalPhase(val label: String) {
    Warmup("Warmup"),
    Work("WORK!"),
    Rest("REST"),
    Cooldown("Cooldown"),
    Complete("FINISHED!")
}

@Composable
fun IntervalTimerScreen() {
    var warmupSec by remember { mutableIntStateOf(5) }
    var workSec by remember { mutableIntStateOf(30) }
    var restSec by remember { mutableIntStateOf(15) }
    var totalSets by remember { mutableIntStateOf(5) }

    var currentSet by remember { mutableIntStateOf(1) }
    var currentPhase by remember { mutableStateOf(IntervalPhase.Warmup) }
    var timeRemaining by remember { mutableIntStateOf(warmupSec) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeRemaining, currentPhase) {
        if (isRunning && currentPhase != IntervalPhase.Complete) {
            delay(1000L)
            if (timeRemaining > 1) {
                timeRemaining -= 1
            } else {
                when (currentPhase) {
                    IntervalPhase.Warmup -> {
                        currentPhase = IntervalPhase.Work
                        timeRemaining = workSec
                    }
                    IntervalPhase.Work -> {
                        if (currentSet >= totalSets) {
                            currentPhase = IntervalPhase.Complete
                            isRunning = false
                        } else {
                            currentPhase = IntervalPhase.Rest
                            timeRemaining = restSec
                        }
                    }
                    IntervalPhase.Rest -> {
                        currentSet += 1
                        currentPhase = IntervalPhase.Work
                        timeRemaining = workSec
                    }
                    else -> {}
                }
            }
        }
    }

    val phaseColor by animateColorAsState(
        targetValue = when (currentPhase) {
            IntervalPhase.Warmup -> Color(0xFFF57F17)
            IntervalPhase.Work -> Color(0xFFD32F2F)
            IntervalPhase.Rest -> Color(0xFF1976D2)
            IntervalPhase.Cooldown -> Color(0xFF388E3C)
            IntervalPhase.Complete -> Color(0xFF4CAF50)
        },
        label = "phaseColor",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Phase Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = phaseColor.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = currentPhase.label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = phaseColor,
                )
                Text(
                    text = "Set $currentSet of $totalSets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = phaseColor,
                )
            }
        }

        // Circular Countdown Display
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(phaseColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$timeRemaining",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = phaseColor,
                )
                Text(
                    text = "SECONDS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 2.sp,
                )
            }
        }

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(containerColor = phaseColor),
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isRunning) "PAUSE" else "START", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = {
                    isRunning = false
                    currentSet = 1
                    currentPhase = IntervalPhase.Warmup
                    timeRemaining = warmupSec
                },
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESET")
            }
        }

        // Interval Config Card (disabled while running)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Interval Settings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                DurationAdjuster("Warmup (sec)", warmupSec, enabled = !isRunning) {
                    warmupSec = it.coerceAtLeast(0)
                    if (currentPhase == IntervalPhase.Warmup) timeRemaining = warmupSec
                }
                DurationAdjuster("Work Interval (sec)", workSec, enabled = !isRunning) {
                    workSec = it.coerceAtLeast(5)
                }
                DurationAdjuster("Rest Interval (sec)", restSec, enabled = !isRunning) {
                    restSec = it.coerceAtLeast(5)
                }
                DurationAdjuster("Total Sets", totalSets, enabled = !isRunning) {
                    totalSets = it.coerceAtLeast(1)
                }
            }
        }
    }
}

@Composable
private fun DurationAdjuster(
    label: String,
    value: Int,
    enabled: Boolean,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
                onClick = { onValueChange(value - 5) },
                enabled = enabled,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
            ) { Text("-5") }
            OutlinedButton(
                onClick = { onValueChange(value - 1) },
                enabled = enabled,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
            ) { Text("-1") }
            Text(
                "$value",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            OutlinedButton(
                onClick = { onValueChange(value + 1) },
                enabled = enabled,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
            ) { Text("+1") }
            OutlinedButton(
                onClick = { onValueChange(value + 5) },
                enabled = enabled,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
            ) { Text("+5") }
        }
    }
}
