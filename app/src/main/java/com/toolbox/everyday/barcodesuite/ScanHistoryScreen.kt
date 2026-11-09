package com.toolbox.everyday.barcodesuite

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScanRecord(val content: String, val format: String, val timestamp: Long)

object ScanHistoryManager {
    private const val FILE_NAME = "scan_history.json"

    fun loadHistory(context: Context): List<ScanRecord> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            val initial = listOf(
                ScanRecord("https://sark.tools", "QR_CODE", System.currentTimeMillis() - 3600000),
                ScanRecord("8901030383451", "EAN_13", System.currentTimeMillis() - 86400000),
                ScanRecord("WIFI:S:MyHomeWiFi;T:WPA;P:SecretPassword123;;", "QR_CODE", System.currentTimeMillis() - 172800000),
            )
            saveHistory(context, initial)
            return initial
        }
        return try {
            val json = file.readText()
            val arr = JSONArray(json)
            val list = mutableListOf<ScanRecord>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ScanRecord(
                        content = obj.getString("content"),
                        format = obj.optString("format", "QR_CODE"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveHistory(context: Context, list: List<ScanRecord>) {
        try {
            val arr = JSONArray()
            list.forEach { item ->
                val obj = JSONObject().apply {
                    put("content", item.content)
                    put("format", item.format)
                    put("timestamp", item.timestamp)
                }
                arr.put(obj)
            }
            File(context.filesDir, FILE_NAME).writeText(arr.toString())
        } catch (_: Exception) {}
    }

    fun addRecord(context: Context, content: String, format: String = "QR_CODE") {
        val current = loadHistory(context).toMutableList()
        current.add(0, ScanRecord(content, format, System.currentTimeMillis()))
        if (current.size > 200) current.removeLast()
        saveHistory(context, current)
    }
}

@Composable
fun ScanHistoryScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val records = remember { mutableStateListOf<ScanRecord>() }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val list = ScanHistoryManager.loadHistory(context)
            withContext(Dispatchers.Main) {
                records.clear()
                records.addAll(list)
            }
        }
    }

    fun clearHistory() {
        scope.launch(Dispatchers.IO) {
            ScanHistoryManager.saveHistory(context, emptyList())
            withContext(Dispatchers.Main) {
                records.clear()
            }
        }
    }

    fun deleteItem(idx: Int) {
        records.removeAt(idx)
        scope.launch(Dispatchers.IO) {
            ScanHistoryManager.saveHistory(context, records)
        }
    }

    val filtered = if (searchQuery.isBlank()) records else records.filter {
        it.content.contains(searchQuery, ignoreCase = true) || it.format.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Scan History") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("${records.size} total items", style = MaterialTheme.typography.labelMedium)
            if (records.isNotEmpty()) {
                TextButton(onClick = { clearHistory() }) {
                    Text("Clear All")
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (searchQuery.isBlank()) "No scans recorded yet." else "No matching scans found.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(filtered) { idx, item ->
                    val isUrl = item.content.startsWith("http://") || item.content.startsWith("https://")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(item.format, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text(sdf.format(Date(item.timestamp)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                if (isUrl) {
                                    IconButton(onClick = {
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.content)))
                                        } catch (_: Exception) {}
                                    }) {
                                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open")
                                    }
                                }
                                IconButton(onClick = { copyToClipboard(context, "Scan", item.content) }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                }
                                IconButton(onClick = { deleteItem(idx) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
