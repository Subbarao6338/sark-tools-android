package com.toolbox.datalab

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.components.TileSelector
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.pow
import kotlin.math.sqrt

data class ParsedDocument(
    val format: String,
    val headers: List<String>,
    val rows: List<List<String>>
)

object DataLabLogic {
    // Real Built-in Datasets
    val IRIS_DATASET_CSV = """
        sepal_length,sepal_width,petal_length,petal_width,species
        5.1,3.5,1.4,0.2,setosa
        4.9,3.0,1.4,0.2,setosa
        4.7,3.2,1.3,0.2,setosa
        7.0,3.2,4.7,1.4,versicolor
        6.4,3.2,4.5,1.5,versicolor
        6.9,3.1,4.9,1.5,versicolor
        6.3,3.3,6.0,2.5,virginica
        5.8,2.7,5.1,1.9,virginica
        7.1,3.0,5.9,2.1,virginica
    """.trimIndent()

    val SALES_DATASET_CSV = """
        Quarter,Region,Category,Revenue,Units_Sold
        Q1,North,Electronics,45200,320
        Q1,South,Home,28400,185
        Q2,North,Electronics,52100,360
        Q2,South,Home,31500,210
        Q3,East,Apparel,39800,420
        Q3,West,Electronics,61200,410
        Q4,North,Apparel,58900,540
        Q4,South,Electronics,72400,490
    """.trimIndent()

    val SENSOR_LOGS_TSV = "Timestamp\tDeviceID\tTemperature\tHumidity\tBatteryPct\n" +
            "2026-04-10T10:00:00Z\tESP32_01\t24.5\t48.2\t98\n" +
            "2026-04-10T10:05:00Z\tESP32_01\t25.1\t47.9\t97\n" +
            "2026-04-10T10:10:00Z\tESP32_01\t26.4\t46.5\t96\n" +
            "2026-04-10T10:15:00Z\tESP32_01\t28.9\t45.0\t95\n" +
            "2026-04-10T10:20:00Z\tESP32_01\t85.0\t12.0\t94"

    fun calculateStats(numbers: List<Double>): Map<String, Double> {
        if (numbers.isEmpty()) return emptyMap()
        val sorted = numbers.sorted()
        val count = sorted.size.toDouble()
        val sum = sorted.sum()
        val mean = sum / count
        val median = if (sorted.size % 2 == 1) {
            sorted[sorted.size / 2]
        } else {
            (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
        }
        val variance = sorted.map { (it - mean).pow(2) }.sum() / count
        val stdDev = sqrt(variance)
        val min = sorted.first()
        val max = sorted.last()

        val q1 = sorted[(sorted.size * 0.25).toInt().coerceIn(0, sorted.size - 1)]
        val q3 = sorted[(sorted.size * 0.75).toInt().coerceIn(0, sorted.size - 1)]
        val iqr = q3 - q1

        // Mode
        val frequencyMap = numbers.groupBy { it }.mapValues { it.value.size }
        val maxFreq = frequencyMap.values.maxOrNull() ?: 1
        val mode = frequencyMap.filter { it.value == maxFreq }.keys.firstOrNull() ?: mean

        return mapOf(
            "Count" to count,
            "Sum" to sum,
            "Mean" to mean,
            "Median" to median,
            "Mode" to mode,
            "Variance" to variance,
            "StdDev" to stdDev,
            "Min" to min,
            "Max" to max,
            "Q1" to q1,
            "Q3" to q3,
            "IQR" to iqr
        )
    }

    fun detectAnomalies(numbers: List<Double>, zThreshold: Double = 2.0): List<Double> {
        val stats = calculateStats(numbers)
        val mean = stats["Mean"] ?: return emptyList()
        val stdDev = stats["StdDev"] ?: return emptyList()
        if (stdDev == 0.0) return emptyList()
        return numbers.filter { kotlin.math.abs(it - mean) / stdDev > zThreshold }
    }

    fun parseDocument(content: String): ParsedDocument {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return ParsedDocument("EMPTY", emptyList(), emptyList())
        }

        // 1. JSON Array
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            return try {
                val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(trimmed)
                if (jsonElement is kotlinx.serialization.json.JsonArray) {
                    val headers = mutableListOf<String>()
                    val rows = mutableListOf<List<String>>()
                    for (element in jsonElement) {
                        if (element is kotlinx.serialization.json.JsonObject) {
                            if (headers.isEmpty()) {
                                headers.addAll(element.keys)
                            }
                            val row = headers.map {
                                element[it]?.toString()?.removeSurrounding("\"") ?: ""
                            }
                            rows.add(row)
                        }
                    }
                    ParsedDocument("JSON", headers, rows)
                } else {
                    ParsedDocument("TEXT", listOf("Line"), trimmed.lines().map { listOf(it) })
                }
            } catch (_: Exception) {
                ParsedDocument("TEXT", listOf("Line"), trimmed.lines().map { listOf(it) })
            }
        }

        // 2. JSON Lines / NDJSON
        if (trimmed.lines().size > 1 && trimmed.lines().first().trim().startsWith("{")) {
            try {
                val headers = mutableListOf<String>()
                val rows = mutableListOf<List<String>>()
                trimmed.lines().filter { it.isNotBlank() }.forEach { line ->
                    val obj = JSONObject(line.trim())
                    if (headers.isEmpty()) {
                        headers.addAll(obj.keys().asSequence().toList())
                    }
                    rows.add(headers.map { obj.optString(it, "") })
                }
                return ParsedDocument("JSONL", headers, rows)
            } catch (_: Exception) {}
        }

        // 3. TSV (Tab separated)
        if (trimmed.lines().any { it.contains("\t") }) {
            val lines = trimmed.lines().filter { it.isNotBlank() }
            val headers = lines.first().split("\t").map { it.trim() }
            val rows = lines.drop(1).map { line ->
                line.split("\t").map { it.trim() }
            }
            return ParsedDocument("TSV", headers, rows)
        }

        // 4. CSV (Comma separated)
        if (trimmed.lines().any { it.contains(",") }) {
            val lines = trimmed.lines().filter { it.isNotBlank() }
            val headers = lines.first().split(",").map { it.trim() }
            val rows = lines.drop(1).map { line ->
                line.split(",").map { it.trim() }
            }
            return ParsedDocument("CSV", headers, rows)
        }

        // 5. Fallback Key-Value or Plain Text
        val lines = trimmed.lines().filter { it.isNotBlank() }
        if (lines.all { it.contains(":") || it.contains("=") }) {
            val delimiter = if (lines.first().contains(":")) ":" else "="
            val rows = lines.map { l ->
                val p = l.split(delimiter, limit = 2)
                listOf(p[0].trim(), p.getOrElse(1) { "" }.trim())
            }
            return ParsedDocument("KEY_VALUE", listOf("Key", "Value"), rows)
        }

        return ParsedDocument("PLAIN_TEXT", listOf("Index", "Content"), lines.mapIndexed { idx, s -> listOf("${idx + 1}", s) })
    }

    fun generateMockUsers(count: Int): List<String> {
        val names = listOf("Alex", "Sam", "Jordan", "Taylor", "Morgan", "Casey", "Subba", "Priya", "Rahul", "Aarav")
        val domains = listOf("gmail.com", "example.org", "outlook.com", "techcorp.io")
        val roles = listOf("Engineering", "Product", "Design", "Marketing", "Security")
        return (1..count).map { idx ->
            val name = names[idx % names.size] + "$idx"
            val email = "${name.lowercase()}@${domains[idx % domains.size]}"
            val role = roles[idx % roles.size]
            "{\"id\": $idx, \"name\": \"$name\", \"email\": \"$email\", \"role\": \"$role\"}"
        }
    }

    fun generateMockOrders(count: Int): List<String> {
        val items = listOf("Laptop", "Monitor", "Desk Chair", "Mechanical Keyboard", "USB-C Hub", "Headphones")
        val statuses = listOf("Completed", "Processing", "Shipped", "Delivered")
        return (1..count).map { idx ->
            val item = items[idx % items.size]
            val amount = 50 + (idx * 37) % 850
            val status = statuses[idx % statuses.size]
            "{\"orderId\": \"ORD-$idx\", \"item\": \"$item\", \"amount\": $amount, \"status\": \"$status\"}"
        }
    }

    fun reconcileLists(listA: List<String>, listB: List<String>): Map<String, List<String>> {
        val setA = listA.toSet()
        val setB = listB.toSet()
        val matched = setA.intersect(setB).toList()
        val onlyInA = setA.subtract(setB).toList()
        val onlyInB = setB.subtract(setA).toList()
        return mapOf("Matched" to matched, "Only in A" to onlyInA, "Only in B" to onlyInB)
    }
}

@Composable
fun DataLabScreen(toolId: String = "science") {
    val tools = listOf("Data Science", "Document Viewer", "Analytics", "Reconciliation", "Mock Generator", "Synthetic Data")
    val initialIdx = when (toolId) {
        "viewer" -> 1
        "adv-data" -> 2
        "reconcile" -> 3
        "mock" -> 4
        "synthetic" -> 5
        else -> 0
    }
    var selectedIdx by remember { mutableIntStateOf(initialIdx) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Tile Selector instead of tabs
        TileSelector(
            options = tools,
            selectedIndex = selectedIdx,
            onSelectIndex = { selectedIdx = it }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedIdx) {
                0, 2 -> DataScienceHubUi()
                1 -> DataViewerUi()
                3 -> ReconciliationUi()
                4, 5 -> MockDataGeneratorUi()
                else -> DataScienceHubUi()
            }
        }
    }
}

@Composable
fun DataScienceHubUi() {
    var rawInput by remember { mutableStateOf("12, 15, 18, 20, 22, 19, 14, 85, 16, 21, 23, 22, 19, 17, 18") }
    var stats by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var anomalies by remember { mutableStateOf<List<Double>>(emptyList()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Data Science, Analytics & Outlier Detection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Comprehensive statistical summaries, quartiles, IQR, and Z-score anomaly detection.")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { rawInput = "5.1, 4.9, 4.7, 7.0, 6.4, 6.9, 6.3, 5.8, 7.1" }) {
                    Text("Iris Sepal")
                }
                OutlinedButton(onClick = { rawInput = "45200, 28400, 52100, 31500, 39800, 61200, 58900, 72400" }) {
                    Text("Sales Revenue")
                }
                OutlinedButton(onClick = { rawInput = "24.5, 25.1, 26.4, 28.9, 85.0" }) {
                    Text("Sensor Telemetry")
                }
            }

            OutlinedTextField(
                value = rawInput,
                onValueChange = { rawInput = it },
                label = { Text("Numbers (comma or space separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                val numbers = rawInput.split(Regex("[,\\s]+")).mapNotNull { it.trim().toDoubleOrNull() }
                stats = DataLabLogic.calculateStats(numbers)
                anomalies = DataLabLogic.detectAnomalies(numbers)
            }) {
                Text("Analyze Dataset")
            }
            if (stats.isNotEmpty()) {
                Text("Statistical Summary & Distribution Metrics:", fontWeight = FontWeight.SemiBold)
                stats.forEach { (k, v) ->
                    Text("$k: ${String.format("%.3f", v)}", fontFamily = FontFamily.Monospace)
                }
                if (anomalies.isNotEmpty()) {
                    Text("Outliers Detected (Z > 2.0): ${anomalies.joinToString(", ")}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                } else {
                    Text("No significant outliers detected.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DataViewerUi() {
    val context = LocalContext.current
    var rawText by remember { mutableStateOf(DataLabLogic.IRIS_DATASET_CSV) }
    var parsedDoc by remember { mutableStateOf(DataLabLogic.parseDocument(rawText)) }
    var pickedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val text = stream.bufferedReader().readText()
                    rawText = text
                    parsedDoc = DataLabLogic.parseDocument(text)
                    pickedFileName = uri.lastPathSegment ?: "Imported File"
                }
            } catch (e: Exception) {
                pickedFileName = "Error reading file: ${e.message}"
            }
        }
    }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Multi-Format Document & Local File Viewer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Supports local file import and direct text parsing for CSV, TSV, JSON, JSONL, XML, and Logs.")

            // Local file picker action button
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FileOpen, contentDescription = null)
                Text("  Pick Local Document / Dataset File")
            }
            if (pickedFileName != null) {
                Text("Loaded File: $pickedFileName", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Text("Pre-Loaded Datasets:", fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    rawText = DataLabLogic.IRIS_DATASET_CSV
                    parsedDoc = DataLabLogic.parseDocument(rawText)
                    pickedFileName = "iris.csv"
                }) { Text("Iris (CSV)") }

                OutlinedButton(onClick = {
                    rawText = DataLabLogic.SALES_DATASET_CSV
                    parsedDoc = DataLabLogic.parseDocument(rawText)
                    pickedFileName = "sales.csv"
                }) { Text("Sales (CSV)") }

                OutlinedButton(onClick = {
                    rawText = DataLabLogic.SENSOR_LOGS_TSV
                    parsedDoc = DataLabLogic.parseDocument(rawText)
                    pickedFileName = "sensor_logs.tsv"
                }) { Text("Telemetry (TSV)") }

                OutlinedButton(onClick = {
                    rawText = "[\n" + DataLabLogic.generateMockUsers(4).joinToString(",\n") + "\n]"
                    parsedDoc = DataLabLogic.parseDocument(rawText)
                    pickedFileName = "users.json"
                }) { Text("Users (JSON)") }
            }

            OutlinedTextField(
                value = rawText,
                onValueChange = {
                    rawText = it
                    parsedDoc = DataLabLogic.parseDocument(it)
                },
                label = { Text("Document Content (${parsedDoc.format})") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
            )

            if (parsedDoc.rows.isNotEmpty()) {
                Text("Parsed Table Preview (${parsedDoc.format} — ${parsedDoc.rows.size} records):", fontWeight = FontWeight.SemiBold)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (parsedDoc.headers.isNotEmpty()) {
                        Text(
                            parsedDoc.headers.joinToString("  |  "),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    parsedDoc.rows.take(20).forEach { row ->
                        Text(row.joinToString("  |  "), fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun ReconciliationUi() {
    var listA by remember { mutableStateOf("Apple\nBanana\nCherry\nDate\nElderberry") }
    var listB by remember { mutableStateOf("Banana\nDate\nElderberry\nFig\nGrape") }
    var result by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Dataset Reconciliation & Audit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = listA, onValueChange = { listA = it }, label = { Text("Dataset A (line-separated)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(value = listB, onValueChange = { listB = it }, label = { Text("Dataset B (line-separated)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = {
                val a = listA.lines().map { it.trim() }.filter { it.isNotEmpty() }
                val b = listB.lines().map { it.trim() }.filter { it.isNotEmpty() }
                result = DataLabLogic.reconcileLists(a, b)
            }) {
                Text("Reconcile Datasets")
            }
            if (result.isNotEmpty()) {
                Text("Matched Records (${result["Matched"]?.size ?: 0}): ${result["Matched"]?.joinToString(", ")}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Only in A (${result["Only in A"]?.size ?: 0}): ${result["Only in A"]?.joinToString(", ")}")
                Text("Only in B (${result["Only in B"]?.size ?: 0}): ${result["Only in B"]?.joinToString(", ")}")
            }
        }
    }
}

@Composable
fun MockDataGeneratorUi() {
    var count by remember { mutableStateOf("5") }
    var schemaType by remember { mutableStateOf("Users") }
    var output by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Mock & Synthetic Data Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = schemaType == "Users", onClick = { schemaType = "Users" }, label = { Text("User Profiles") })
                FilterChip(selected = schemaType == "Orders", onClick = { schemaType = "Orders" }, label = { Text("E-Commerce Orders") })
            }
            OutlinedTextField(value = count, onValueChange = { count = it }, label = { Text("Number of Records") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                val n = count.toIntOrNull() ?: 5
                output = if (schemaType == "Users") {
                    DataLabLogic.generateMockUsers(n.coerceIn(1, 50)).joinToString(",\n")
                } else {
                    DataLabLogic.generateMockOrders(n.coerceIn(1, 50)).joinToString(",\n")
                }
            }) {
                Text("Generate Mock Records")
            }
            if (output.isNotBlank()) {
                Text("[\n$output\n]", fontFamily = FontFamily.Monospace)
            }
        }
    }
}
