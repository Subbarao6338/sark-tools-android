package omni.toolbox.ui.screens.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun TextDiffScreen(navController: NavHostController) {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }

    val diffLines = remember(text1, text2) {
        if (text1.isEmpty() && text2.isEmpty()) emptyList()
        else TextAnalysisUtils.computeLineDiff(text1, text2)
    }

    val addedCount = diffLines.count { it.type == DiffType.ADDED }
    val removedCount = diffLines.count { it.type == DiffType.REMOVED }
    val unchangedCount = diffLines.count { it.type == DiffType.UNCHANGED }

    ToolScreen(
        title = "Text Diff",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = text1,
                onValueChange = {
                    text1 = it
                    showResult = true
                },
                label = { Text("Original Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = text2,
                onValueChange = {
                    text2 = it
                    showResult = true
                },
                label = { Text("Modified Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { showResult = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compare")
            }

            if (showResult && (text1.isNotEmpty() || text2.isNotEmpty())) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Comparison Result", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DiffStatChip("Added (+$addedCount)", Color(0xFF2E7D32), Modifier.weight(1f))
                    DiffStatChip("Removed (-$removedCount)", Color(0xFFC62828), Modifier.weight(1f))
                    DiffStatChip("Unchanged ($unchangedCount)", MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (text1 == text2) {
                            Text("Texts are identical.", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                        } else {
                            diffLines.forEach { line ->
                                DiffLineView(line)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiffStatChip(label: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DiffLineView(diffLine: DiffLine) {
    val (bgColor, textColor, prefix) = when (diffLine.type) {
        DiffType.ADDED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "+ ")
        DiffType.REMOVED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "- ")
        DiffType.UNCHANGED -> Triple(Color.Transparent, MaterialTheme.colorScheme.onSurface, "  ")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$prefix${diffLine.text}",
            color = textColor,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
