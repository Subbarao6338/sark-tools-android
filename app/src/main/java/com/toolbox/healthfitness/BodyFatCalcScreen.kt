package com.toolbox.healthfitness

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.log10

@Composable
fun BodyFatCalcScreen() {
    var isMale by remember { mutableStateOf(true) }
    var heightCm by remember { mutableStateOf("175") }
    var neckCm by remember { mutableStateOf("38") }
    var waistCm by remember { mutableStateOf("85") }
    var hipCm by remember { mutableStateOf("95") }

    val h = heightCm.toDoubleOrNull() ?: 0.0
    val neck = neckCm.toDoubleOrNull() ?: 0.0
    val waist = waistCm.toDoubleOrNull() ?: 0.0
    val hip = hipCm.toDoubleOrNull() ?: 0.0

    val bodyFat = remember(isMale, h, neck, waist, hip) {
        try {
            if (isMale) {
                if (h > 0 && waist > neck && neck > 0) {
                    val denom = 1.0324 - (0.19077 * log10(waist - neck)) + (0.15456 * log10(h))
                    (495.0 / denom) - 450.0
                } else null
            } else {
                if (h > 0 && (waist + hip) > neck && neck > 0) {
                    val denom = 1.29579 - (0.35004 * log10(waist + hip - neck)) + (0.22100 * log10(h))
                    (495.0 / denom) - 450.0
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    val category = when {
        bodyFat == null -> "Enter valid measurements"
        isMale -> when {
            bodyFat < 6.0 -> "Essential Fat"
            bodyFat <= 13.0 -> "Athletes"
            bodyFat <= 17.0 -> "Fitness"
            bodyFat <= 24.0 -> "Average"
            else -> "Above Average"
        }
        else -> when {
            bodyFat < 14.0 -> "Essential Fat"
            bodyFat <= 20.0 -> "Athletes"
            bodyFat <= 24.0 -> "Fitness"
            bodyFat <= 31.0 -> "Average"
            else -> "Above Average"
        }
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (bodyFat != null && bodyFat > 0) String.format(Locale.US, "%.1f %%", bodyFat) else "-- %",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = isMale,
                onClick = { isMale = true },
                label = { Text("Male") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isMale,
                onClick = { isMale = false },
                label = { Text("Female") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = heightCm,
            onValueChange = { heightCm = it },
            label = { Text("Height (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = neckCm,
            onValueChange = { neckCm = it },
            label = { Text("Neck Circumference (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = waistCm,
            onValueChange = { waistCm = it },
            label = { Text("Waist Circumference (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isMale) {
            OutlinedTextField(
                value = hipCm,
                onValueChange = { hipCm = it },
                label = { Text("Hip Circumference (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("US Navy Standard Categories", fontWeight = FontWeight.Bold)
                if (isMale) {
                    Text("• Essential Fat: 2 - 5%", style = MaterialTheme.typography.bodySmall)
                    Text("• Athletes: 6 - 13%", style = MaterialTheme.typography.bodySmall)
                    Text("• Fitness: 14 - 17%", style = MaterialTheme.typography.bodySmall)
                    Text("• Acceptable: 18 - 24%", style = MaterialTheme.typography.bodySmall)
                    Text("• Obese: 25%+", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("• Essential Fat: 10 - 13%", style = MaterialTheme.typography.bodySmall)
                    Text("• Athletes: 14 - 20%", style = MaterialTheme.typography.bodySmall)
                    Text("• Fitness: 21 - 24%", style = MaterialTheme.typography.bodySmall)
                    Text("• Acceptable: 25 - 31%", style = MaterialTheme.typography.bodySmall)
                    Text("• Obese: 32%+", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
