package com.toolbox.conversion

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
import androidx.compose.material3.OutlinedButton
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
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

object EpicConvertersLogic {

    fun calculateSubnet(cidrIp: String): Map<String, String> {
        return try {
            val parts = cidrIp.trim().split("/")
            if (parts.size != 2) return mapOf("Error" to "Format must be IP/prefix (e.g. 192.168.1.10/24)")
            val ipStr = parts[0]
            val prefix = parts[1].toInt()
            if (prefix !in 0..32) return mapOf("Error" to "Prefix must be 0-32")

            val ipBytes = InetAddress.getByName(ipStr).address
            var ipInt = 0L
            for (b in ipBytes) {
                ipInt = (ipInt shl 8) or (b.toInt() and 0xFF).toLong()
            }

            val maskInt = if (prefix == 0) 0L else (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
            val netInt = ipInt and maskInt
            val bcastInt = netInt or ((1L shl (32 - prefix)) - 1L)

            fun toIp(n: Long) = "${(n shr 24) and 0xFF}.${(n shr 16) and 0xFF}.${(n shr 8) and 0xFF}.${n and 0xFF}"

            val netAddress = toIp(netInt)
            val netmask = toIp(maskInt)
            val broadcast = toIp(bcastInt)
            val totalHosts = if (prefix >= 31) 0 else ((1L shl (32 - prefix)) - 2L)
            val firstHost = if (prefix >= 31) "N/A" else toIp(netInt + 1)
            val lastHost = if (prefix >= 31) "N/A" else toIp(bcastInt - 1)

            mapOf(
                "Network Address" to netAddress,
                "Netmask" to netmask,
                "Broadcast" to broadcast,
                "Usable Hosts" to totalHosts.toString(),
                "Host Range" to "$firstHost - $lastHost"
            )
        } catch (e: Exception) {
            mapOf("Error" to "Invalid IP address: ${e.message}")
        }
    }

    fun calculateWordRank(word: String): Long {
        val w = word.uppercase().filter { it.isLetter() }
        if (w.isEmpty()) return 0L
        fun fact(n: Int): Long = if (n <= 1) 1L else n * fact(n - 1)

        var rank = 1L
        for (i in 0 until w.length) {
            var smaller = 0
            for (j in i + 1 until w.length) {
                if (w[j] < w[i]) smaller++
            }
            rank += smaller * fact(w.length - 1 - i)
        }
        return rank
    }

    fun calculateDateDifference(d1: LocalDate, d2: LocalDate): String {
        val (start, end) = if (d1.isBefore(d2)) Pair(d1, d2) else Pair(d2, d1)
        val p = Period.between(start, end)
        val totalDays = java.time.temporal.ChronoUnit.DAYS.between(start, end)
        return "${p.years} years, ${p.months} months, ${p.days} days (Total: $totalDays days)"
    }

    fun convertJsonToCsv(jsonStr: String): String {
        return try {
            val trimmed = jsonStr.trim()
            val array = if (trimmed.startsWith("[")) org.json.JSONArray(trimmed) else org.json.JSONArray().put(org.json.JSONObject(trimmed))
            if (array.length() == 0) return ""
            val first = array.getJSONObject(0)
            val headers = mutableListOf<String>()
            val keys = first.keys()
            while (keys.hasNext()) headers.add(keys.next())

            val sb = StringBuilder(headers.joinToString(",") + "\n")
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val row = headers.map { k ->
                    val v = obj.optString(k, "")
                    if (v.contains(",") || v.contains("\"")) "\"${v.replace("\"", "\"\"")}\"" else v
                }
                sb.append(row.joinToString(",")).append("\n")
            }
            sb.toString().trim()
        } catch (e: Exception) {
            "Error converting JSON to CSV: ${e.message}"
        }
    }

    fun convertCsvToJson(csvStr: String): String {
        return try {
            val lines = csvStr.lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) return "[]"
            val headers = lines[0].split(",").map { it.trim().removeSurrounding("\"") }
            val array = org.json.JSONArray()
            for (i in 1 until lines.size) {
                val row = lines[i].split(",").map { it.trim().removeSurrounding("\"") }
                val obj = org.json.JSONObject()
                headers.forEachIndexed { idx, h ->
                    obj.put(h, row.getOrElse(idx) { "" })
                }
                array.put(obj)
            }
            array.toString(2)
        } catch (e: Exception) {
            "Error converting CSV to JSON: ${e.message}"
        }
    }

    fun base64Encode(text: String): String = Base64.getEncoder().encodeToString(text.toByteArray(StandardCharsets.UTF_8))
    fun base64Decode(b64: String): String = try {
        String(Base64.getDecoder().decode(b64.trim()), StandardCharsets.UTF_8)
    } catch (e: Exception) { "Invalid Base64: ${e.message}" }

    fun convertTimezone(fromZone: String, toZone: String): String {
        return try {
            val now = ZonedDateTime.now(ZoneId.of(fromZone))
            val target = now.withZoneSameInstant(ZoneId.of(toZone))
            val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
            "From ($fromZone): ${now.format(fmt)}\nTo ($toZone): ${target.format(fmt)}"
        } catch (e: Exception) {
            "Timezone conversion error: ${e.message}"
        }
    }
}

@Composable
fun EpicConvertersCalculatorsScreen(toolId: String = "subnet") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "subnet" to "Subnet Calculator",
        "word-rank" to "Word Rank",
        "datediff" to "Date Difference",
        "json-csv" to "JSON ↔ CSV",
        "base64" to "Base64 Tool",
        "timezone" to "Timezone Converter",
        "yaml" to "YAML / JSON",
        "xml-json" to "XML / JSON",
        "batch" to "Batch Converter"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.take(3).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(3).take(3).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(6).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "subnet" -> SubnetCalcUi()
            "word-rank" -> WordRankUi()
            "datediff" -> DateDiffUi()
            "json-csv", "yaml", "xml-json", "batch" -> JsonCsvUi()
            "base64" -> Base64Ui()
            "timezone" -> TimezoneUi()
            else -> SubnetCalcUi()
        }
    }
}

@Composable
fun SubnetCalcUi() {
    var cidr by remember { mutableStateOf("192.168.1.45/24") }
    var results by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("IPv4 Subnet Calculator (CIDR)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = cidr, onValueChange = { cidr = it }, label = { Text("IP/Prefix (e.g. 10.0.0.1/16)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { results = EpicConvertersLogic.calculateSubnet(cidr) }) {
                Text("Calculate Subnet")
            }
            results.forEach { (k, v) ->
                Text("$k: $v", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun WordRankUi() {
    var word by remember { mutableStateOf("STRING") }
    var rank by remember { mutableStateOf<Long?>(null) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Word Rank / Lexicographical Permutation Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = word, onValueChange = { word = it }, label = { Text("Word") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { rank = EpicConvertersLogic.calculateWordRank(word) }) {
                Text("Compute Lexicographic Rank")
            }
            rank?.let {
                Text("Lexicographical Rank: #$it", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun DateDiffUi() {
    var d1Str by remember { mutableStateOf("2024-01-01") }
    var d2Str by remember { mutableStateOf("2026-11-09") }
    var result by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Date Difference Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = d1Str, onValueChange = { d1Str = it }, label = { Text("Start Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = d2Str, onValueChange = { d2Str = it }, label = { Text("End Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                try {
                    val d1 = LocalDate.parse(d1Str.trim())
                    val d2 = LocalDate.parse(d2Str.trim())
                    result = EpicConvertersLogic.calculateDateDifference(d1, d2)
                } catch (e: Exception) {
                    result = "Invalid date format. Use YYYY-MM-DD"
                }
            }) {
                Text("Calculate Interval")
            }
            if (result.isNotBlank()) {
                Text(result, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun JsonCsvUi() {
    var input by remember { mutableStateOf("[{\"name\": \"Alice\", \"role\": \"Lead\"}, {\"name\": \"Bob\", \"role\": \"Staff\"}]") }
    var output by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Data Format Converter (JSON / CSV / YAML / XML)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Input String") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { output = EpicConvertersLogic.convertJsonToCsv(input) }) {
                    Text("JSON → CSV")
                }
                OutlinedButton(onClick = { output = EpicConvertersLogic.convertCsvToJson(input) }) {
                    Text("CSV → JSON")
                }
            }
            if (output.isNotBlank()) {
                Text("Converted Result:", fontWeight = FontWeight.SemiBold)
                Text(output, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun Base64Ui() {
    var text by remember { mutableStateOf("Sark Tools & Epic Tools suite") }
    var encoded by remember { mutableStateOf("") }
    var decoded by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Base64 String & Binary Tool", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Text / Base64") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { encoded = EpicConvertersLogic.base64Encode(text) }) {
                    Text("Base64 Encode")
                }
                OutlinedButton(onClick = { decoded = EpicConvertersLogic.base64Decode(text) }) {
                    Text("Base64 Decode")
                }
            }
            if (encoded.isNotBlank()) Text("Encoded: $encoded", fontFamily = FontFamily.Monospace)
            if (decoded.isNotBlank()) Text("Decoded: $decoded", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun TimezoneUi() {
    var fromZone by remember { mutableStateOf("UTC") }
    var toZone by remember { mutableStateOf("Asia/Kolkata") }
    var result by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("World Timezone Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = fromZone, onValueChange = { fromZone = it }, label = { Text("From Timezone (e.g. UTC, America/New_York)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = toZone, onValueChange = { toZone = it }, label = { Text("To Timezone (e.g. Asia/Kolkata, Europe/London)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { result = EpicConvertersLogic.convertTimezone(fromZone.trim(), toZone.trim()) }) {
                Text("Convert Time")
            }
            if (result.isNotBlank()) {
                Text(result, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
