package com.toolbox.healthfitness

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun MacroCalcScreen() {
    var calorieInput by remember { mutableStateOf("2200") }
    var dietGoal by remember { mutableStateOf(0) }

    val calories = calorieInput.toIntOrNull() ?: 2200

    val (pPct, cPct, fPct) = when (dietGoal) {
        0 -> Triple(0.25, 0.50, 0.25)
        1 -> Triple(0.40, 0.35, 0.25)
        2 -> Triple(0.30, 0.50, 0.20)
        else -> Triple(0.30, 0.10, 0.60)
    }

    val proteinGrams = (calories * pPct) / 4.0
    val carbGrams = (calories * cPct) / 4.0
    val fatGrams = (calories * fPct) / 9.0

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
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$calories kcal / day",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Daily Target Energy Intake",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedTextField(
            value = calorieInput,
            onValueChange = { calorieInput = it },
            label = { Text("Daily Calories (kcal)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Nutritional Goal & Strategy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Balanced", "Cut / Protein", "Bulking", "Keto").forEachIndexed { index, label ->
                FilterChip(
                    selected = dietGoal == index,
                    onClick = { dietGoal = index },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Protein", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(String.format(Locale.US, "%.0fg", proteinGrams), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${(pPct * 100).toInt()}% • ${(proteinGrams * 4).toInt()} kcal", style = MaterialTheme.typography.labelSmall)
                }
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Carbohydrates", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(String.format(Locale.US, "%.0fg", carbGrams), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${(cPct * 100).toInt()}% • ${(carbGrams * 4).toInt()} kcal", style = MaterialTheme.typography.labelSmall)
                }
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Fats", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(String.format(Locale.US, "%.0fg", fatGrams), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${(fPct * 100).toInt()}% • ${(fatGrams * 9).toInt()} kcal", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
