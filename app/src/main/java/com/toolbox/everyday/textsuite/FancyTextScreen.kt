package com.toolbox.everyday.textsuite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

data class FancyStyle(val name: String, val transform: (String) -> String)

@Composable
fun FancyTextScreen() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("Cool Text 123") }

    val styles = listOf(
        FancyStyle("Bold (Serif)") { toMathFont(it, 0x1D400, 0x1D41A, 0x1D7CE) },
        FancyStyle("Italic (Serif)") { toMathFont(it, 0x1D434, 0x1D44E, null) },
        FancyStyle("Bold Italic") { toMathFont(it, 0x1D468, 0x1D482, null) },
        FancyStyle("Script / Cursive") { toScript(it) },
        FancyStyle("Gothic / Fraktur") { toGothic(it) },
        FancyStyle("Double-Struck (Blackboard)") { toDoubleStruck(it) },
        FancyStyle("Monospace") { toMathFont(it, 0x1D670, 0x1D68A, 0x1D7F6) },
        FancyStyle("Circled") { toCircled(it) },
        FancyStyle("Squared") { toSquared(it) },
        FancyStyle("Inverted / Upside Down") { toUpsideDown(it) },
        FancyStyle("Strikethrough") { it.map { c -> "$c\u0336" }.joinToString("") },
        FancyStyle("Underline") { it.map { c -> "$c\u0332" }.joinToString("") },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Your Text") },
            modifier = Modifier.fillMaxWidth(),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(styles) { style ->
                val converted = style.transform(input)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(style.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(converted, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = { copyToClipboard(context, style.name, converted) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }
            }
        }
    }
}

private fun toMathFont(text: String, upperBase: Int, lowerBase: Int, digitBase: Int?): String {
    val sb = StringBuilder()
    for (cp in text.codePoints().toArray()) {
        when {
            cp in 'A'.code..'Z'.code -> sb.append(String(Character.toChars(upperBase + (cp - 'A'.code))))
            cp in 'a'.code..'z'.code -> sb.append(String(Character.toChars(lowerBase + (cp - 'a'.code))))
            digitBase != null && cp in '0'.code..'9'.code -> sb.append(String(Character.toChars(digitBase + (cp - '0'.code))))
            else -> sb.append(String(Character.toChars(cp)))
        }
    }
    return sb.toString()
}

private fun toScript(text: String): String =
    toMathFont(text, 0x1D49C, 0x1D4B6, null)

private fun toGothic(text: String): String =
    toMathFont(text, 0x1D504, 0x1D51E, null)

private fun toDoubleStruck(text: String): String =
    toMathFont(text, 0x1D538, 0x1D552, 0x1D7D8)

private fun toCircled(text: String): String {
    val sb = StringBuilder()
    for (c in text) {
        when (c) {
            in 'A'..'Z' -> sb.append(Character.toChars(0x24B6 + (c - 'A')))
            in 'a'..'z' -> sb.append(Character.toChars(0x24D0 + (c - 'a')))
            in '1'..'9' -> sb.append(Character.toChars(0x2460 + (c - '1')))
            '0' -> sb.append("⓪")
            else -> sb.append(c)
        }
    }
    return sb.toString()
}

private fun toSquared(text: String): String {
    val sb = StringBuilder()
    for (c in text) {
        when (c) {
            in 'A'..'Z' -> sb.append(Character.toChars(0x1F130 + (c - 'A')))
            in 'a'..'z' -> sb.append(Character.toChars(0x1F130 + (c - 'a')))
            else -> sb.append(c)
        }
    }
    return sb.toString()
}

private fun toUpsideDown(text: String): String {
    val normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?,!.'"
    val flip   = "ɐqɔpǝɟɓɥıɾʞןɯuodbɹsʇnʌʍxʎzⱯᗺƆᗡƎℲ⅁HIſʞꞀWNOԀÒᴚS⊥∩ΛMX⅄Z0ƖᄅƐㄣϛ9ㄥ86¿'¡˙,"
    val map = normal.zip(flip).toMap()
    return text.reversed().map { map[it] ?: it }.joinToString("")
}
