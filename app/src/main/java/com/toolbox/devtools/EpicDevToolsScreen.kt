package com.toolbox.devtools

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
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

object EpicDevToolsLogic {

    fun formatSql(sql: String): String {
        if (sql.isBlank()) return ""
        val keywords = listOf(
            "SELECT", "FROM", "WHERE", "GROUP BY", "HAVING", "ORDER BY",
            "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "OUTER JOIN", "JOIN",
            "INSERT INTO", "VALUES", "UPDATE", "SET", "DELETE FROM", "UNION ALL", "UNION",
            "LIMIT", "OFFSET", "AND", "OR", "ON", "AS"
        )
        var formatted = sql.trim().replace(Regex("\\s+"), " ")
        for (kw in keywords) {
            formatted = formatted.replace(Regex("(?i)\\b$kw\\b"), kw)
        }
        val breakKeywords = listOf("SELECT", "FROM", "WHERE", "GROUP BY", "HAVING", "ORDER BY", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "JOIN", "VALUES", "SET", "LIMIT")
        for (kw in breakKeywords) {
            formatted = formatted.replace(Regex("(?i)\\b$kw\\b"), "\n$kw")
        }
        return formatted.trim()
    }

    fun formatXml(xml: String): String {
        return try {
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val result = StreamResult(StringWriter())
            val source = StreamSource(StringReader(xml.trim()))
            transformer.transform(source, result)
            result.writer.toString().trim()
        } catch (e: Exception) {
            "Error formatting XML: ${e.message}"
        }
    }

    fun jsonToTs(name: String, jsonStr: String): String {
        return try {
            val cleanName = if (name.isBlank()) "RootObject" else name.trim()
            val trimmed = jsonStr.trim()
            if (trimmed.startsWith("[")) {
                val array = JSONArray(trimmed)
                if (array.length() > 0 && array.get(0) is JSONObject) {
                    val obj = array.getJSONObject(0)
                    return buildTsInterface(cleanName, obj) + "\n\nexport type ${cleanName}List = ${cleanName}[];"
                }
                return "export type ${cleanName}List = any[];"
            } else {
                val obj = JSONObject(trimmed)
                buildTsInterface(cleanName, obj)
            }
        } catch (e: Exception) {
            "// Invalid JSON: ${e.message}"
        }
    }

    private fun buildTsInterface(name: String, obj: JSONObject): String {
        val sb = StringBuilder("export interface $name {\n")
        val keys = obj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            val v = obj.get(k)
            val type = when (v) {
                is String -> "string"
                is Int, is Long, is Double -> "number"
                is Boolean -> "boolean"
                is JSONObject -> "Record<string, any>"
                is JSONArray -> "any[]"
                JSONObject.NULL -> "null | any"
                else -> "any"
            }
            sb.append("  ").append(k).append(": ").append(type).append(";\n")
        }
        sb.append("}")
        return sb.toString()
    }

    fun formatKql(kql: String): String {
        if (kql.isBlank()) return ""
        val lines = kql.split("|").map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return kql.trim()
        return lines.first() + "\n" + lines.drop(1).joinToString("\n") { "| " + it }
    }

    fun parseJwt(token: String): Pair<String, String> {
        val parts = token.trim().split(".")
        if (parts.size < 2) return Pair("Invalid JWT", "JWT must contain at least 2 parts (header.payload)")
        fun decode(s: String): String {
            return try {
                var padded = s.replace("-", "+").replace("_", "/")
                while (padded.length % 4 != 0) padded += "="
                val bytes = Base64.getDecoder().decode(padded)
                val json = String(bytes, StandardCharsets.UTF_8)
                JSONObject(json).toString(2)
            } catch (e: Exception) {
                "Error decoding: ${e.message}"
            }
        }
        return Pair(decode(parts[0]), decode(parts[1]))
    }

    fun minifyCode(code: String, type: String): String {
        return when (type) {
            "JSON" -> try {
                val t = code.trim()
                if (t.startsWith("[")) JSONArray(t).toString() else JSONObject(t).toString()
            } catch (e: Exception) { "Error minifying JSON" }
            "SQL" -> code.trim().replace(Regex("\\s+"), " ")
            else -> code.lines().map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
        }
    }

    fun parseCron(expr: String): String {
        val parts = expr.trim().split(Regex("\\s+"))
        if (parts.size < 5) return "Invalid cron expression (needs min 5 parts: min hour day month weekday)"
        val (min, hr, dom, mon, dow) = parts
        return "Minute: $min, Hour: $hr, Day of Month: $dom, Month: $mon, Day of Week: $dow"
    }

    fun buildSql(table: String, action: String, columns: String, condition: String): String {
        val t = if (table.isBlank()) "my_table" else table.trim()
        val cols = if (columns.isBlank()) "*" else columns.trim()
        val where = if (condition.isBlank()) "" else " WHERE " + condition.trim()
        return when (action) {
            "SELECT" -> "SELECT $cols FROM $t$where;"
            "INSERT" -> "INSERT INTO $t ($cols) VALUES (...);"
            "UPDATE" -> "UPDATE $t SET $cols$where;"
            "DELETE" -> "DELETE FROM $t$where;"
            else -> "SELECT $cols FROM $t$where;"
        }
    }

    fun generateMarkdownTable(headers: List<String>, rows: List<List<String>>): String {
        val h = "| " + headers.joinToString(" | ") + " |"
        val sep = "| " + headers.joinToString(" | ") { "---" } + " |"
        val r = rows.joinToString("\n") { row ->
            "| " + row.joinToString(" | ") + " |"
        }
        return "$h\n$sep\n$r"
    }
}

@Composable
fun EpicDevToolsScreen(toolId: String = "sql") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "sql" to "SQL Formatter",
        "sql-builder" to "SQL Builder",
        "xml-fmt" to "XML Formatter",
        "json-ts" to "JSON to TS",
        "kusto" to "KQL Formatter",
        "jwt" to "JWT Debugger",
        "diff" to "Diff Viewer",
        "cron" to "Cron Parser",
        "minifier" to "Code Minifier",
        "md-table" to "Markdown Table",
        "url" to "URL Tool",
        "api-tester" to "REST API Tester",
        "inspiration" to "Code Inspiration"
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
            tools.take(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(4).take(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.drop(8).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "sql" -> SqlFormatterUi()
            "sql-builder" -> SqlBuilderUi()
            "xml-fmt" -> XmlFormatterUi()
            "json-ts" -> JsonToTsUi()
            "kusto" -> KqlFormatterUi()
            "jwt" -> JwtDebuggerUi()
            "diff" -> DiffViewerUi()
            "cron" -> CronParserUi()
            "minifier" -> CodeMinifierUi()
            "md-table" -> MarkdownTableUi()
            "url" -> UrlToolUi()
            "api-tester" -> ApiTesterUi()
            "inspiration" -> CodeInspirationUi()
            else -> SqlFormatterUi()
        }
    }
}

@Composable
fun SqlFormatterUi() {
    var input by remember { mutableStateOf("SELECT id, name, email FROM users WHERE active = 1 ORDER BY name ASC") }
    var output by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("SQL Formatter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Input SQL") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = { output = EpicDevToolsLogic.formatSql(input) }) {
                Text("Format SQL")
            }
            if (output.isNotBlank()) {
                Text("Formatted SQL:", fontWeight = FontWeight.SemiBold)
                Text(output, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun SqlBuilderUi() {
    var table by remember { mutableStateOf("customers") }
    var action by remember { mutableStateOf("SELECT") }
    var columns by remember { mutableStateOf("id, name, balance") }
    var condition by remember { mutableStateOf("balance > 100") }
    var result by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("SQL Builder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = table, onValueChange = { table = it }, label = { Text("Table Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = columns, onValueChange = { columns = it }, label = { Text("Columns") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = condition, onValueChange = { condition = it }, label = { Text("Condition (WHERE)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { result = EpicDevToolsLogic.buildSql(table, action, columns, condition) }) {
                Text("Generate SQL Query")
            }
            if (result.isNotBlank()) {
                Text(result, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun XmlFormatterUi() {
    var input by remember { mutableStateOf("<note><to>User</to><from>Dev</from><heading>Reminder</heading><body>Don't forget the tools!</body></note>") }
    var output by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("XML Formatter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("XML String") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = { output = EpicDevToolsLogic.formatXml(input) }) {
                Text("Format XML")
            }
            if (output.isNotBlank()) {
                Text(output, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun JsonToTsUi() {
    var interfaceName by remember { mutableStateOf("UserProfile") }
    var input by remember { mutableStateOf("{\"id\": 101, \"username\": \"alex\", \"isActive\": true, \"roles\": [\"admin\"]}") }
    var output by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("JSON to TypeScript Interface", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = interfaceName, onValueChange = { interfaceName = it }, label = { Text("Interface Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("JSON Input") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = { output = EpicDevToolsLogic.jsonToTs(interfaceName, input) }) {
                Text("Generate Interface")
            }
            if (output.isNotBlank()) {
                Text(output, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun KqlFormatterUi() {
    var input by remember { mutableStateOf("SecurityEvent | where TimeGenerated > ago(1h) | where EventID == 4624 | summarize count() by Account") }
    var output by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("KQL (Kusto Query) Formatter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("KQL Query") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = { output = EpicDevToolsLogic.formatKql(input) }) {
                Text("Format KQL")
            }
            if (output.isNotBlank()) {
                Text(output, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun JwtDebuggerUi() {
    var token by remember { mutableStateOf("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c") }
    var header by remember { mutableStateOf("") }
    var payload by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("JWT Debugger", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("JWT Token") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                val res = EpicDevToolsLogic.parseJwt(token)
                header = res.first
                payload = res.second
            }) {
                Text("Decode JWT")
            }
            if (header.isNotBlank()) {
                Text("Header:", fontWeight = FontWeight.SemiBold)
                Text(header, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Payload:", fontWeight = FontWeight.SemiBold)
                Text(payload, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun DiffViewerUi() {
    var text1 by remember { mutableStateOf("Hello World\nKotlin 1.9\nJetpack Compose") }
    var text2 by remember { mutableStateOf("Hello World\nKotlin 2.0\nJetpack Compose\nMaterial 3 Expressive") }
    var diffResult by remember { mutableStateOf(listOf<String>()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Visual Text & Code Diff", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = text1, onValueChange = { text1 = it }, label = { Text("Original Text") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = text2, onValueChange = { text2 = it }, label = { Text("Modified Text") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = {
                val lines1 = text1.lines()
                val lines2 = text2.lines()
                val res = mutableListOf<String>()
                for (l in lines1) {
                    if (l !in lines2) res.add("- $l")
                }
                for (l in lines2) {
                    if (l !in lines1) res.add("+ $l") else res.add("  $l")
                }
                diffResult = res
            }) {
                Text("Compute Line Diff")
            }
            if (diffResult.isNotEmpty()) {
                Text("Diff Result:", fontWeight = FontWeight.SemiBold)
                diffResult.forEach { line ->
                    val color = when {
                        line.startsWith("+") -> MaterialTheme.colorScheme.primary
                        line.startsWith("-") -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(line, color = color, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun CronParserUi() {
    var cron by remember { mutableStateOf("*/15 0 1,15 * 1-5") }
    var result by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Cron Schedule Parser", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = cron, onValueChange = { cron = it }, label = { Text("Cron Expression") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { result = EpicDevToolsLogic.parseCron(cron) }) {
                Text("Parse Cron")
            }
            if (result.isNotBlank()) {
                Text(result, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun CodeMinifierUi() {
    var code by remember { mutableStateOf("{\n  \"name\": \"Toolbox\",\n  \"version\": 2\n}") }
    var type by remember { mutableStateOf("JSON") }
    var minified by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Code & Data Minifier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("JSON", "SQL", "Plain").forEach { t ->
                    FilterChip(selected = type == t, onClick = { type = t }, label = { Text(t) })
                }
            }
            OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Input Code") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = { minified = EpicDevToolsLogic.minifyCode(code, type) }) {
                Text("Minify")
            }
            if (minified.isNotBlank()) {
                Text(minified, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun MarkdownTableUi() {
    var headers by remember { mutableStateOf("Feature, Supported, Notes") }
    var rows by remember { mutableStateOf("Offline Mode, Yes, 100% on device\nTelemetry, No, Zero tracking") }
    var result by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Markdown Table Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = headers, onValueChange = { headers = it }, label = { Text("Headers (comma-separated)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = rows, onValueChange = { rows = it }, label = { Text("Rows (comma-separated, newline per row)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = {
                val h = headers.split(",").map { it.trim() }
                val r = rows.lines().filter { it.isNotBlank() }.map { it.split(",").map { cell -> cell.trim() } }
                result = EpicDevToolsLogic.generateMarkdownTable(h, r)
            }) {
                Text("Generate Table")
            }
            if (result.isNotBlank()) {
                Text(result, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun UrlToolUi() {
    var rawUrl by remember { mutableStateOf("https://example.com/search?query=jetpack+compose&sort=desc") }
    var encoded by remember { mutableStateOf("") }
    var decoded by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("URL Encoder & Decoder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = rawUrl, onValueChange = { rawUrl = it }, label = { Text("URL / Text") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { encoded = URLEncoder.encode(rawUrl, "UTF-8") }) {
                    Text("Encode")
                }
                OutlinedButton(onClick = { decoded = URLDecoder.decode(rawUrl, "UTF-8") }) {
                    Text("Decode")
                }
            }
            if (encoded.isNotBlank()) Text("Encoded: $encoded", fontFamily = FontFamily.Monospace)
            if (decoded.isNotBlank()) Text("Decoded: $decoded", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun ApiTesterUi() {
    var method by remember { mutableStateOf("GET") }
    var url by remember { mutableStateOf("https://api.github.com/zen") }
    var headers by remember { mutableStateOf("Accept: application/json") }
    var body by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Ready to send test request (Client-side offline simulation supported)") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("REST API Client & Tester", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("GET", "POST", "PUT", "DELETE").forEach { m ->
                    FilterChip(selected = method == m, onClick = { method = m }, label = { Text(m) })
                }
            }
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Endpoint URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = headers, onValueChange = { headers = it }, label = { Text("Headers") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { status = "Status: 200 OK (Simulated REST response for $method $url)" }) {
                Text("Send Request")
            }
            Text(status, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CodeInspirationUi() {
    val templates = listOf(
        "Compose Button" to "Button(onClick = { /* action */ }) {\n    Text(\"Click Me\")\n}",
        "Kotlin Coroutines" to "viewModelScope.launch(Dispatchers.IO) {\n    val data = fetchData()\n    withContext(Dispatchers.Main) { updateUi(data) }\n}",
        "DataStore Read" to "val flow: Flow<String> = context.dataStore.data.map { it[KEY] ?: \"default\" }"
    )
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Code Inspiration & Architecture Snippets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            templates.forEach { (title, snippet) ->
                Text(title, fontWeight = FontWeight.Bold)
                Text(snippet, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}
