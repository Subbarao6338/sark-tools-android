package com.toolbox.conversion.devsuite

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun HtmlEntityScreen() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("<div class=\"header\">Hello & Welcome!</div>") }
    var output by rememberSaveable { mutableStateOf("") }

    val entityMap = mapOf(
        "&" to "&amp;",
        "<" to "&lt;",
        ">" to "&gt;",
        "\"" to "&quot;",
        "'" to "&#39;",
        "©" to "&copy;",
        "®" to "&reg;",
        "™" to "&trade;",
        "€" to "&euro;",
        "£" to "&pound;",
        "¥" to "&yen;",
        "₹" to "&#8377;",
        " " to "&nbsp;"
    )

    fun encodeHtml(text: String): String {
        val sb = StringBuilder()
        for (c in text) {
            val str = c.toString()
            if (entityMap.containsKey(str) && str != " ") {
                sb.append(entityMap[str])
            } else if (c.code > 127) {
                sb.append("&#${c.code};")
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun decodeHtml(text: String): String {
        var res = text
        for ((char, entity) in entityMap) {
            res = res.replace(entity, char)
        }
        val numRegex = Regex("&#(\\d+);")
        res = numRegex.replace(res) { match ->
            val code = match.groupValues[1].toIntOrNull()
            if (code != null) String(Character.toChars(code)) else match.value
        }
        val hexRegex = Regex("&#x([0-9a-fA-F]+);")
        res = hexRegex.replace(res) { match ->
            val code = match.groupValues[1].toIntOrNull(16)
            if (code != null) String(Character.toChars(code)) else match.value
        }
        return res
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Input Text / HTML") },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { output = encodeHtml(input) },
                modifier = Modifier.weight(1f),
            ) {
                Text("Encode Entities")
            }
            Button(
                onClick = { output = decodeHtml(input) },
                modifier = Modifier.weight(1f),
            ) {
                Text("Decode Entities")
            }
        }

        if (output.isNotEmpty()) {
            OutlinedTextField(
                value = output,
                onValueChange = { output = it },
                label = { Text("Result") },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
            )

            OutlinedButton(
                onClick = { copyToClipboard(context, "HTML Entity Result", output) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Copy Result")
            }
        }
    }
}
