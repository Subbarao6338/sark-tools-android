package com.toolbox.conversion.basiccalc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.copyToClipboard
import java.text.DecimalFormat

@Composable
fun BasicCalculatorScreen() {
    val context = LocalContext.current
    var expression by rememberSaveable { mutableStateOf("") }
    var resultText by rememberSaveable { mutableStateOf("0") }
    var memoryValue by rememberSaveable { mutableDoubleStateOf(0.0) }
    var showHistory by rememberSaveable { mutableStateOf(false) }
    val history = rememberSaveable { mutableStateListOf<Pair<String, String>>() }

    fun calculate(expr: String): String {
        if (expr.isBlank()) return "0"
        return try {
            val sanitized = expr.replace("×", "*").replace("÷", "/")
            val res = evaluateBasicExpression(sanitized)
            val df = DecimalFormat("#,##0.########")
            df.format(res)
        } catch (_: Exception) {
            "Error"
        }
    }

    fun onInput(char: String) {
        val lastChar = expression.lastOrNull()
        if (char in "+-×÷") {
            if (expression.isEmpty()) {
                if (char == "-") expression = "-"
                return
            }
            if (lastChar != null && lastChar in "+-×÷") {
                expression = expression.dropLast(1) + char
                return
            }
        }
        if (char == ".") {
            val tokens = expression.split(Regex("[+\\-×÷]"))
            if (tokens.last().contains(".")) return
        }
        expression += char
        val preview = calculate(expression)
        if (preview != "Error") {
            resultText = preview
        }
    }

    fun onEquals() {
        if (expression.isBlank()) return
        val finalRes = calculate(expression)
        if (finalRes != "Error") {
            history.add(0, expression to finalRes)
            if (history.size > 50) history.removeLast()
            expression = finalRes.replace(",", "")
            resultText = finalRes
        } else {
            resultText = "Error"
        }
    }

    fun onPercent() {
        if (expression.isBlank()) return
        try {
            val num = calculate(expression).replace(",", "").toDouble() / 100.0
            val df = DecimalFormat("#,##0.########")
            val formatted = df.format(num)
            expression = formatted.replace(",", "")
            resultText = formatted
        } catch (_: Exception) {}
    }

    fun onBackspace() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            resultText = if (expression.isEmpty()) "0" else calculate(expression)
        }
    }

    fun onClear() {
        expression = ""
        resultText = "0"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (memoryValue != 0.0) "M: ${DecimalFormat("#.##").format(memoryValue)}" else "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = { showHistory = !showHistory }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                }

                if (showHistory) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(history) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(item.first, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("= ${item.second}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState(Int.MAX_VALUE)),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            text = expression.ifEmpty { "0" },
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(modifier = Modifier.weight(1f), onClick = { memoryValue = 0.0 }) { Text("MC") }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    expression += DecimalFormat("#.########").format(memoryValue)
                    resultText = calculate(expression)
                },
            ) { Text("MR") }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    val curr = resultText.replace(",", "").toDoubleOrNull() ?: 0.0
                    memoryValue += curr
                },
            ) { Text("M+") }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    val curr = resultText.replace(",", "").toDoubleOrNull() ?: 0.0
                    memoryValue -= curr
                },
            ) { Text("M-") }
        }

        val buttonRows = listOf(
            listOf("C", "⌫", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("±", "0", ".", "="),
        )

        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { label ->
                    val isOp = label in listOf("÷", "×", "-", "+", "=")
                    val isAction = label in listOf("C", "⌫", "%", "±")

                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = when {
                                label == "=" -> MaterialTheme.colorScheme.primary
                                isOp -> MaterialTheme.colorScheme.primaryContainer
                                isAction -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                            contentColor = when {
                                label == "=" -> MaterialTheme.colorScheme.onPrimary
                                isOp -> MaterialTheme.colorScheme.onPrimaryContainer
                                isAction -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                        ),
                        onClick = {
                            when (label) {
                                "C" -> onClear()
                                "⌫" -> onBackspace()
                                "%" -> onPercent()
                                "±" -> {
                                    if (expression.startsWith("-")) {
                                        expression = expression.drop(1)
                                    } else if (expression.isNotEmpty()) {
                                        expression = "-$expression"
                                    }
                                    resultText = calculate(expression)
                                }
                                "=" -> onEquals()
                                else -> onInput(label)
                            }
                        },
                    ) {
                        if (label == "⌫") {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace")
                        } else {
                            Text(text = label, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

fun evaluateBasicExpression(expr: String): Double {
    val tokens = mutableListOf<String>()
    var numBuf = StringBuilder()
    var i = 0
    while (i < expr.length) {
        val c = expr[i]
        if (c in '0'..'9' || c == '.') {
            numBuf.append(c)
        } else if (c in "+-*/") {
            if (c == '-' && (i == 0 || expr[i - 1] in "+-*/")) {
                numBuf.append(c)
            } else {
                if (numBuf.isNotEmpty()) {
                    tokens.add(numBuf.toString())
                    numBuf = StringBuilder()
                }
                tokens.add(c.toString())
            }
        }
        i++
    }
    if (numBuf.isNotEmpty()) tokens.add(numBuf.toString())

    if (tokens.isEmpty()) return 0.0

    val intermediate = mutableListOf<String>()
    var idx = 0
    while (idx < tokens.size) {
        val tok = tokens[idx]
        if (tok == "*" || tok == "/") {
            val prev = intermediate.removeAt(intermediate.lastIndex).toDouble()
            val next = tokens[++idx].toDouble()
            val res = if (tok == "*") prev * next else prev / next
            intermediate.add(res.toString())
        } else {
            intermediate.add(tok)
        }
        idx++
    }

    var result = intermediate[0].toDouble()
    idx = 1
    while (idx < intermediate.size) {
        val op = intermediate[idx]
        val next = intermediate[++idx].toDouble()
        if (op == "+") result += next
        else if (op == "-") result -= next
        idx++
    }
    return result
}
