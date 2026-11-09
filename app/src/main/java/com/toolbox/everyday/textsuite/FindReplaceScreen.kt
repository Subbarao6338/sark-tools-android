package com.toolbox.everyday.textsuite

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun FindReplaceScreen() {
    val context = LocalContext.current
    var sourceText by rememberSaveable { mutableStateOf("The quick brown fox jumps over the lazy dog. Fox is quick!") }
    var findText by rememberSaveable { mutableStateOf("fox") }
    var replaceText by rememberSaveable { mutableStateOf("cat") }
    var isRegex by rememberSaveable { mutableStateOf(false) }
    var matchCase by rememberSaveable { mutableStateOf(false) }

    val matchCount = remember(sourceText, findText, isRegex, matchCase) {
        if (findText.isEmpty()) 0
        else try {
            if (isRegex) {
                val opts = if (matchCase) emptySet() else setOf(RegexOption.IGNORE_CASE)
                val regex = Regex(findText, opts)
                regex.findAll(sourceText).count()
            } else {
                if (matchCase) {
                    var count = 0
                    var idx = 0
                    while (sourceText.indexOf(findText, idx).also { idx = it } != -1) {
                        count++
                        idx += findText.length
                    }
                    count
                } else {
                    var count = 0
                    var idx = 0
                    val lowerSrc = sourceText.lowercase()
                    val lowerFind = findText.lowercase()
                    while (lowerSrc.indexOf(lowerFind, idx).also { idx = it } != -1) {
                        count++
                        idx += lowerFind.length
                    }
                    count
                }
            }
        } catch (_: Exception) {
            -1
        }
    }

    fun executeReplace(): String {
        if (findText.isEmpty()) return sourceText
        return try {
            if (isRegex) {
                val opts = if (matchCase) emptySet() else setOf(RegexOption.IGNORE_CASE)
                val regex = Regex(findText, opts)
                regex.replace(sourceText, replaceText)
            } else {
                if (matchCase) {
                    sourceText.replace(findText, replaceText)
                } else {
                    val regex = Regex(Regex.escape(findText), RegexOption.IGNORE_CASE)
                    regex.replace(sourceText, replaceText)
                }
            }
        } catch (_: Exception) {
            sourceText
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = sourceText,
            onValueChange = { sourceText = it },
            label = { Text("Original Text") },
            modifier = Modifier.fillMaxWidth().height(160.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = findText,
                onValueChange = { findText = it },
                label = { Text("Find") },
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = replaceText,
                onValueChange = { replaceText = it },
                label = { Text("Replace with") },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = matchCase,
                onClick = { matchCase = !matchCase },
                label = { Text("Match Case (Aa)") },
            )
            FilterChip(
                selected = isRegex,
                onClick = { isRegex = !isRegex },
                label = { Text("Regex (.*)") },
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Matches found:")
                Text(
                    text = if (matchCount == -1) "Invalid Regex" else "$matchCount occurrences",
                    fontWeight = FontWeight.Bold,
                    color = if (matchCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Button(
            onClick = {
                sourceText = executeReplace()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Replace All")
        }

        OutlinedButton(
            onClick = { copyToClipboard(context, "Replaced Text", sourceText) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Copy Text")
        }
    }
}
