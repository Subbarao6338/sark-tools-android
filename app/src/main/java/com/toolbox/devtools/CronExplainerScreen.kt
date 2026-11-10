package com.toolbox.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CronExplainerScreen() {
    var cronExpr by remember { mutableStateOf("0 12 * * MON-FRI") }
    var explanation by remember { mutableStateOf("") }

    fun explain(expr: String): String {
        val parts = expr.trim().split(Regex("\\s+"))
        if (parts.size < 5) return "Invalid expression. Cron format requires at least 5 tokens: [Minute] [Hour] [DayOfMonth] [Month] [DayOfWeek]"

        val (min, hr, dom, mon, dow) = parts.take(5)
        val minDesc = when (min) {
            "*" -> "every minute"
            "0" -> "at the top of the hour (minute 0)"
            else -> "at minute $min"
        }
        val hrDesc = when (hr) {
            "*" -> "of every hour"
            "12" -> "at 12:00 PM (noon)"
            "0" -> "at midnight (00:00)"
            else -> "at hour $hr"
        }
        val domDesc = when (dom) {
            "*" -> "every day of the month"
            else -> "on day-of-month $dom"
        }
        val monDesc = when (mon) {
            "*" -> "every month"
            else -> "in month $mon"
        }
        val dowDesc = when (dow) {
            "*" -> "every day of the week"
            "MON-FRI", "1-5" -> "Monday through Friday"
            "0,6", "SUN,SAT" -> "on weekends (Saturday & Sunday)"
            else -> "on day-of-week $dow"
        }

        return "Runs $minDesc $hrDesc, $domDesc, $monDesc, $dowDesc."
    }

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
                Text("Cron Expression Natural Explainer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Translate standard Unix cron syntax into clear, unambiguous natural language schedules.")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = cronExpr == "0 12 * * MON-FRI", onClick = {
                cronExpr = "0 12 * * MON-FRI"
                explanation = explain(cronExpr)
            }, label = { Text("Weekdays Noon") })

            FilterChip(selected = cronExpr == "*/15 * * * *", onClick = {
                cronExpr = "*/15 * * * *"
                explanation = explain(cronExpr)
            }, label = { Text("Every 15m") })

            FilterChip(selected = cronExpr == "0 0 1 * *", onClick = {
                cronExpr = "0 0 1 * *"
                explanation = explain(cronExpr)
            }, label = { Text("1st of Month") })
        }

        OutlinedTextField(
            value = cronExpr,
            onValueChange = { cronExpr = it },
            label = { Text("Cron Expression") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
        )

        Button(onClick = { explanation = explain(cronExpr) }, modifier = Modifier.fillMaxWidth()) {
            Text("Explain Expression")
        }

        if (explanation.isNotBlank()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Human-Readable Schedule:", style = MaterialTheme.typography.labelMedium)
                    Text(explanation, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
