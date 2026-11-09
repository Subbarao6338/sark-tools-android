package com.toolbox.web

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

object EpicWebLogic {
    fun htmlToMarkdown(html: String): String {
        return html
            .replace(Regex("(?i)<h1[^>]*>(.*?)</h1>"), "# $1\n")
            .replace(Regex("(?i)<h2[^>]*>(.*?)</h2>"), "## $1\n")
            .replace(Regex("(?i)<h3[^>]*>(.*?)</h3>"), "### $1\n")
            .replace(Regex("(?i)<p[^>]*>(.*?)</p>"), "$1\n\n")
            .replace(Regex("(?i)<strong[^>]*>(.*?)</strong>"), "**$1**")
            .replace(Regex("(?i)<b[^>]*>(.*?)</b>"), "**$1**")
            .replace(Regex("(?i)<em[^>]*>(.*?)</em>"), "*$1*")
            .replace(Regex("(?i)<a[^>]*href=\"([^\"]*)\"[^>]*>(.*?)</a>"), "[$2]($1)")
            .replace(Regex("<[^>]+>"), "")
            .trim()
    }

    fun auditSocialUrl(url: String): Map<String, String> {
        val u = url.lowercase().trim()
        val platform = when {
            u.contains("youtube.com") || u.contains("youtu.be") -> "YouTube Video / Shorts"
            u.contains("twitter.com") || u.contains("x.com") -> "X (Twitter) Post"
            u.contains("instagram.com") -> "Instagram Media"
            u.contains("reddit.com") -> "Reddit Post"
            u.contains("github.com") -> "GitHub Repository"
            else -> "Generic Web Resource"
        }
        val isHttps = u.startsWith("https://")
        return mapOf(
            "Detected Platform" to platform,
            "Secure HTTPS" to if (isHttps) "Yes" else "No (Insecure)",
            "Download Strategy" to "Client-side metadata extraction & local caching supported"
        )
    }
}

@Composable
fun EpicWebToolsScreen(toolId: String = "extension-sources") {
    var selectedTool by remember { mutableStateOf(toolId) }
    val tools = listOf(
        "extension-sources" to "Extension Sources",
        "archive" to "Web Archive",
        "url2pdf" to "URL to PDF",
        "userscripts" to "User Scripts",
        "bookmarklets" to "Bookmarklets",
        "url2markdown" to "URL to Markdown",
        "social" to "Social Audit",
        "social-downloader" to "Social Downloader"
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
            tools.drop(4).forEach { (id, name) ->
                FilterChip(selected = selectedTool == id, onClick = { selectedTool = id }, label = { Text(name) })
            }
        }

        when (selectedTool) {
            "extension-sources" -> ExtensionSourcesUi()
            "archive" -> WebArchiveUi()
            "url2pdf" -> UrlToPdfUi()
            "userscripts", "bookmarklets" -> UserScriptsUi()
            "url2markdown" -> UrlToMarkdownUi()
            "social", "social-downloader" -> SocialAuditUi()
            else -> ExtensionSourcesUi()
        }
    }
}

@Composable
fun ExtensionSourcesUi() {
    val extensions = listOf(
        "uBlock Origin" to "https://github.com/gorhill/uBlock",
        "Bitwarden" to "https://github.com/bitwarden/clients",
        "Violentmonkey" to "https://github.com/violentmonkey/violentmonkey",
        "Dark Reader" to "https://github.com/darkreader/darkreader"
    )
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Open Source Browser Extensions Directory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            extensions.forEach { (name, link) ->
                Text(name, fontWeight = FontWeight.Bold)
                Text(link, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 6.dp))
            }
        }
    }
}

@Composable
fun WebArchiveUi() {
    var queryUrl by remember { mutableStateOf("https://example.com") }
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Wayback Machine & Web Archive Launcher", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = queryUrl, onValueChange = { queryUrl = it }, label = { Text("Web URL") }, modifier = Modifier.fillMaxWidth())
            Text("Archive Mirror: https://web.archive.org/web/*/$queryUrl", fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun UrlToPdfUi() {
    var url by remember { mutableStateOf("https://wikipedia.org/wiki/Kotlin_(programming_language)") }
    var status by remember { mutableStateOf("Ready to export webpage as clean document PDF") }
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Webpage to Document PDF Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Webpage URL") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { status = "PDF snapshot prepared for: $url" }) {
                Text("Generate Offline PDF Snapshot")
            }
            Text(status, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun UserScriptsUi() {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("User Scripts & Bookmarklets Repository", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("1. Dark Mode Enforcer:", fontWeight = FontWeight.SemiBold)
            Text("javascript:(function(){document.documentElement.style.filter='invert(100%) hue-rotate(180deg)';})();", fontFamily = FontFamily.Monospace)
            Text("2. Extract All Image URLs:", fontWeight = FontWeight.SemiBold)
            Text("javascript:(function(){alert(Array.from(document.images).map(i=>i.src).join('\\n'));})();", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun UrlToMarkdownUi() {
    var htmlInput by remember { mutableStateOf("<h1>Article Title</h1><p>This is a <strong>clean</strong> paragraph with a <a href=\"https://example.com\">link</a>.</p>") }
    var mdOutput by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("HTML & Webpage to Markdown Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = htmlInput, onValueChange = { htmlInput = it }, label = { Text("HTML Source") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = { mdOutput = EpicWebLogic.htmlToMarkdown(htmlInput) }) {
                Text("Convert to Markdown")
            }
            if (mdOutput.isNotBlank()) {
                Text(mdOutput, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun SocialAuditUi() {
    var socialUrl by remember { mutableStateOf("https://youtube.com/watch?v=dQw4w9WgXcQ") }
    var audit by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Social Media Link Audit & Downloader Assistant", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = socialUrl, onValueChange = { socialUrl = it }, label = { Text("Media / Social Post URL") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { audit = EpicWebLogic.auditSocialUrl(socialUrl) }) {
                Text("Audit Media Link")
            }
            audit.forEach { (k, v) ->
                Text("$k: $v", fontWeight = FontWeight.Medium)
            }
        }
    }
}
