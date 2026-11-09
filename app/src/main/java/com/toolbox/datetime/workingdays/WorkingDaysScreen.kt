package com.toolbox.datetime.workingdays

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun WorkingDaysScreen() {
    val context = LocalContext.current

    val today = rememberSaveable { LocalDate.now().toString() }
    val nextMonth = rememberSaveable { LocalDate.now().plusMonths(1).toString() }

    var startDateStr by rememberSaveable { mutableStateOf(today) }
    var endDateStr by rememberSaveable { mutableStateOf(nextMonth) }

    var excludeSaturday by rememberSaveable { mutableStateOf(true) }
    var excludeSunday by rememberSaveable { mutableStateOf(true) }
    var holidaysStr by rememberSaveable { mutableStateOf("0") }

    data class WorkingDaysResult(
        val totalDays: Long,
        val workingDays: Long,
        val weekendDays: Long,
        val holidays: Long,
    )

    val calcResult: WorkingDaysResult? = try {
        val start = LocalDate.parse(startDateStr)
        val end = LocalDate.parse(endDateStr)
        if (start.isAfter(end)) null
        else {
            val total = ChronoUnit.DAYS.between(start, end) + 1
            var working = 0L
            var weekends = 0L
            var curr = start
            while (!curr.isAfter(end)) {
                val dow = curr.dayOfWeek
                val isSat = (dow == DayOfWeek.SATURDAY && excludeSaturday)
                val isSun = (dow == DayOfWeek.SUNDAY && excludeSunday)
                if (isSat || isSun) {
                    weekends++
                } else {
                    working++
                }
                curr = curr.plusDays(1)
            }
            val h = (holidaysStr.toLongOrNull() ?: 0L).coerceAtMost(working)
            working -= h
            WorkingDaysResult(total, working, weekends, h)
        }
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
                Text("Select Date Range (YYYY-MM-DD)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = startDateStr,
                        onValueChange = { startDateStr = it },
                        label = { Text("Start Date") },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = endDateStr,
                        onValueChange = { endDateStr = it },
                        label = { Text("End Date") },
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = excludeSaturday, onCheckedChange = { excludeSaturday = it })
                        Text("Exclude Sat")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = excludeSunday, onCheckedChange = { excludeSunday = it })
                        Text("Exclude Sun")
                    }
                }

                OutlinedTextField(
                    value = holidaysStr,
                    onValueChange = { holidaysStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Public Holidays (count)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }

        if (calcResult != null) {
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
                    Text("Working Days Result", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "${calcResult.workingDays} Business Days",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Days", style = MaterialTheme.typography.labelSmall)
                            Text("${calcResult.totalDays}", fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Weekends", style = MaterialTheme.typography.labelSmall)
                            Text("${calcResult.weekendDays}", fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Holidays", style = MaterialTheme.typography.labelSmall)
                            Text("${calcResult.holidays}", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { copyToClipboard(context, "Working Days", "${calcResult.workingDays} working days") },
                    ) {
                        Text("Copy Result")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            ) {
                Text(
                    text = "Invalid date format or start date is after end date. Use YYYY-MM-DD.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}
