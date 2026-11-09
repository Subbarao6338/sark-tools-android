package com.toolbox.conversion.devsuite

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RegexTesterScreen() {
    var regexPattern by rememberSaveable { mutableStateOf("([a-zA-Z0-9_.-]+)@([a-zA-Z0-9_-]+)\\.([a-zA-Z]{2,5})") }
    var testText by rememberSaveable { mutableStateOf("Contact us at support@sark.tools or dev-team@example.org for questions!") }
    var ignoreCase by rememberSaveable { mutableStateOf(false) }
    var multiline by rememberSaveable { mutableStateOf(false) }
    var dotAll by rememberSaveable { mutableStateOf(false) }

    data class RegexMatchResult(val value: String, val range: IntRange, val groups: List<String>)

    var matches = emptyList<RegexMatchResult>()
    var regexError: String? = null

    try {
        val options = mutableSetOf<RegexOption>()
        if (ignoreCase) options.add(RegexOption.IGNORE_CASE)
        if (multiline) options.add(RegexOption.MULTILINE)
        if (dotAll) options.add(RegexOption.DOT_MATCHES_ALL)

        val reg = Regex(regexPattern, options)
        matches = reg.findAll(testText).map { m ->
            RegexMatchResult(m.value, m.range, m.groupValues.drop(1))
        }.toList()
    } catch (e: Exception) {
        regexError = e.message
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = regexPattern,
            onValueChange = { regexPattern = it },
            label = { Text("Regular Expression") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = ignoreCase,
                onClick = { ignoreCase = !ignoreCase },
                label = { Text("Ignore Case (i)") },
            )
            FilterChip(
                selected = multiline,
                onClick = { multiline = !multiline },
                label = { Text("Multiline (m)") },
            )
            FilterChip(
                selected = dotAll,
                onClick = { dotAll = !dotAll },
                label = { Text("Dot All (s)") },
            )
        }

        OutlinedTextField(
            value = testText,
            onValueChange = { testText = it },
            label = { Text("Test String") },
            modifier = Modifier.fillMaxWidth().height(140.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
        )

        if (regexError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            ) {
                Text(
                    text = "Regex Syntax Error: $regexError",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Matches: ${matches.size}", fontWeight = FontWeight.Bold)
            }

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                if (matches.isEmpty()) {
                    Text(
                        text = "No matches found.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(matches) { idx, match ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Match #${idx + 1}: \"${match.value}\"", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    Text("Indices: ${match.range.first} .. ${match.range.last}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (match.groups.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        match.groups.forEachIndexed { gIdx, gVal ->
                                            Text("Group ${gIdx + 1}: $gVal", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
