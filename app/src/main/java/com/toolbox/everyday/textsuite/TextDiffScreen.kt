package com.toolbox.everyday.textsuite

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class DiffType { EQUAL, INSERT, DELETE, MODIFY }

data class LineDiff(
    val type: DiffType,
    val lineA: String?,
    val lineB: String?,
    val lineNumA: Int?,
    val lineNumB: Int?,
)

@Composable
fun TextDiffScreen() {
    var textA by rememberSaveable { mutableStateOf("First line\nSecond line has changed\nOriginal line\nFourth line") }
    var textB by rememberSaveable { mutableStateOf("First line\nSecond line is modified\nNew added line\nOriginal line\nFourth line") }
    var ignoreCase by rememberSaveable { mutableStateOf(false) }

    val diffs = computeSimpleDiff(textA.lines(), textB.lines(), ignoreCase)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = textA,
                onValueChange = { textA = it },
                label = { Text("Original (A)") },
                modifier = Modifier.weight(1f).height(140.dp),
            )
            OutlinedTextField(
                value = textB,
                onValueChange = { textB = it },
                label = { Text("Modified (B)") },
                modifier = Modifier.weight(1f).height(140.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FilterChip(
                selected = ignoreCase,
                onClick = { ignoreCase = !ignoreCase },
                label = { Text("Ignore Case") },
            )
            val added = diffs.count { it.type == DiffType.INSERT }
            val removed = diffs.count { it.type == DiffType.DELETE }
            val modified = diffs.count { it.type == DiffType.MODIFY }
            Text(
                "+$added  -$removed  ~$modified",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                itemsIndexed(diffs) { _, diff ->
                    val bgColor = when (diff.type) {
                        DiffType.INSERT -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                        DiffType.DELETE -> Color(0xFFC62828).copy(alpha = 0.2f)
                        DiffType.MODIFY -> Color(0xFFF57F17).copy(alpha = 0.2f)
                        DiffType.EQUAL -> Color.Transparent
                    }

                    val prefix = when (diff.type) {
                        DiffType.INSERT -> "+ "
                        DiffType.DELETE -> "- "
                        DiffType.MODIFY -> "~ "
                        DiffType.EQUAL -> "  "
                    }

                    val lineText = when (diff.type) {
                        DiffType.DELETE -> diff.lineA ?: ""
                        DiffType.INSERT -> diff.lineB ?: ""
                        DiffType.MODIFY -> "A: ${diff.lineA}\nB: ${diff.lineB}"
                        DiffType.EQUAL -> diff.lineA ?: ""
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = prefix,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = when (diff.type) {
                                DiffType.INSERT -> Color(0xFF2E7D32)
                                DiffType.DELETE -> Color(0xFFC62828)
                                DiffType.MODIFY -> Color(0xFFF57F17)
                                DiffType.EQUAL -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                        Text(
                            text = lineText,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

fun computeSimpleDiff(linesA: List<String>, linesB: List<String>, ignoreCase: Boolean): List<LineDiff> {
    val result = mutableListOf<LineDiff>()
    var i = 0
    var j = 0

    fun match(a: String, b: String): Boolean =
        if (ignoreCase) a.equals(b, ignoreCase = true) else a == b

    while (i < linesA.size || j < linesB.size) {
        if (i < linesA.size && j < linesB.size && match(linesA[i], linesB[j])) {
            result.add(LineDiff(DiffType.EQUAL, linesA[i], linesB[j], i + 1, j + 1))
            i++; j++
        } else if (i < linesA.size && linesB.contains(linesA[i])) {
            result.add(LineDiff(DiffType.INSERT, null, linesB[j], null, j + 1))
            j++
        } else if (j < linesB.size && linesA.contains(linesB[j])) {
            result.add(LineDiff(DiffType.DELETE, linesA[i], null, i + 1, null))
            i++
        } else if (i < linesA.size && j < linesB.size) {
            result.add(LineDiff(DiffType.MODIFY, linesA[i], linesB[j], i + 1, j + 1))
            i++; j++
        } else if (i < linesA.size) {
            result.add(LineDiff(DiffType.DELETE, linesA[i], null, i + 1, null))
            i++
        } else {
            result.add(LineDiff(DiffType.INSERT, null, linesB[j], null, j + 1))
            j++
        }
    }
    return result
}
