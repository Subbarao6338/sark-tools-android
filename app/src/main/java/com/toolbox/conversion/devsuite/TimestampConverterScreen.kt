package com.toolbox.conversion.devsuite

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun TimestampConverterScreen() {
    val context = LocalContext.current
    var epochInput by rememberSaveable { mutableStateOf((System.currentTimeMillis() / 1000).toString()) }
    var dateStringInput by rememberSaveable { mutableStateOf("") }

    val epochVal = epochInput.toLongOrNull()
    val millis = if (epochVal != null) {
        if (epochInput.length <= 10) epochVal * 1000L else epochVal
    } else 0L

    val localFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
    }
    val utcFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    val localDateStr = if (millis > 0) localFormatter.format(Date(millis)) else "Invalid Epoch"
    val utcDateStr = if (millis > 0) utcFormatter.format(Date(millis)) else "Invalid Epoch"

    fun setNow() {
        epochInput = (System.currentTimeMillis() / 1000).toString()
    }

    fun parseDate() {
        if (dateStringInput.isBlank()) return
        val formats = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy"
        )
        for (pattern in formats) {
            try {
                val parser = SimpleDateFormat(pattern, Locale.getDefault())
                val d = parser.parse(dateStringInput)
                if (d != null) {
                    epochInput = (d.time / 1000).toString()
                    return
                }
            } catch (_: Exception) {}
        }
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
                Text("Unix Timestamp to Date", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = epochInput,
                    onValueChange = { epochInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Timestamp (Seconds or Millis)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Button(onClick = { setNow() }) {
                    Text("Current Timestamp (Now)")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Local Time (${TimeZone.getDefault().id})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(localDateStr, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onPrimaryContainer)

                Spacer(modifier = Modifier.height(4.dp))
                Text("UTC / ISO-8601", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(utcDateStr, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onPrimaryContainer)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { copyToClipboard(context, "Local Time", localDateStr) }, modifier = Modifier.weight(1f)) {
                        Text("Copy Local")
                    }
                    OutlinedButton(onClick = { copyToClipboard(context, "UTC Time", utcDateStr) }, modifier = Modifier.weight(1f)) {
                        Text("Copy UTC")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Date to Unix Timestamp", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = dateStringInput,
                    onValueChange = { dateStringInput = it },
                    label = { Text("e.g. 2026-12-31 23:59:59") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = { parseDate() }) {
                    Text("Parse to Timestamp")
                }
            }
        }
    }
}
