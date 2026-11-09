package com.toolbox.conversion.healthsuite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun PregnancyCalculatorScreen() {
    val context = LocalContext.current

    val initialDate = rememberSaveable { LocalDate.now().minusWeeks(12).toString() }
    var inputDateStr by rememberSaveable { mutableStateOf(initialDate) }
    var calculationMethod by rememberSaveable { mutableStateOf("LMP") }

    data class PregnancyInfo(
        val dueDate: LocalDate,
        val gestationalWeeks: Long,
        val gestationalDays: Long,
        val trimester: String,
        val daysRemaining: Long,
    )

    val info: PregnancyInfo? = try {
        val inputDate = LocalDate.parse(inputDateStr)
        val lmpDate = if (calculationMethod == "LMP") inputDate else inputDate.minusDays(14)
        val dueDate = lmpDate.plusDays(280)
        val today = LocalDate.now()
        val totalDays = ChronoUnit.DAYS.between(lmpDate, today)
        val weeks = totalDays / 7
        val days = totalDays % 7
        val remDays = ChronoUnit.DAYS.between(today, dueDate).coerceAtLeast(0)

        val trimester = when {
            weeks < 13 -> "First Trimester (Weeks 1 - 12)"
            weeks < 27 -> "Second Trimester (Weeks 13 - 26)"
            else -> "Third Trimester (Weeks 27 - 40+)"
        }
        PregnancyInfo(dueDate, weeks, days, trimester, remDays)
    } catch (_: Exception) {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Calculation Method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = calculationMethod == "LMP",
                        onClick = { calculationMethod = "LMP" },
                        label = { Text("Last Menstrual Period") },
                    )
                    FilterChip(
                        selected = calculationMethod == "Conception",
                        onClick = { calculationMethod = "Conception" },
                        label = { Text("Conception Date") },
                    )
                }

                OutlinedTextField(
                    value = inputDateStr,
                    onValueChange = { inputDateStr = it },
                    label = { Text(if (calculationMethod == "LMP") "First Day of LMP (YYYY-MM-DD)" else "Conception Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (info != null) {
            val dtf = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Estimated Due Date (EDD)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        info.dueDate.format(dtf),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Gestational Age: ${info.gestationalWeeks} weeks, ${info.gestationalDays} days",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(info.trimester, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("${info.daysRemaining} days remaining", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)

                    OutlinedButton(onClick = { copyToClipboard(context, "Due Date", info.dueDate.format(dtf)) }) {
                        Text("Copy Due Date")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            ) {
                Text("Invalid date format. Please use YYYY-MM-DD.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}
