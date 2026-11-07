package com.toolbox.conversion.percentage

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.copyToClipboard
import kotlin.math.abs
import kotlin.math.floor

enum class PercentMode(
    val label: String,
    val description: String,
) {
    PercentOf(
        label = "X% of Y",
        description = "Calculate what a percentage of a number is — e.g. 20% of 500 = 100",
    ),
    WhatPercent(
        label = "What %",
        description = "Find what percentage one number is of another — e.g. 30 is what % of 200 = 15%",
    ),
    PercentChange(
        label = "% Change",
        description = "Percentage increase or decrease from an original value to a new value.",
    ),
    PercentDifference(
        label = "% Difference",
        description = "Calculate percentage difference between two positive values: |V1 - V2| / ((V1 + V2)/2) × 100",
    ),
    ReversePercent(
        label = "Reverse %",
        description = "Find the original value before a percentage was applied (e.g. final is 120 after 20% increase/markup).",
    ),
}

@Composable
fun PercentageScreen() {
    var mode by rememberSaveable { mutableIntStateOf(0) }
    var input1 by rememberSaveable { mutableStateOf("") }
    var input2 by rememberSaveable { mutableStateOf("") }
    var committedResult by rememberSaveable { mutableStateOf<PercentResult?>(null) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun calculate() {
        focusManager.clearFocus()
        committedResult = calculatePercentResult(mode, input1, input2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Horizontally scrollable mode chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PercentMode.entries.forEachIndexed { index, percentMode ->
                FilterChip(
                    selected = mode == index,
                    onClick = {
                        mode = index
                        input1 = ""
                        input2 = ""
                        committedResult = null
                    },
                    label = { Text(percentMode.label) },
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mode description
        Text(
            text = PercentMode.entries[mode].description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val (label1, placeholder1, label2, placeholder2) = when (mode) {
                    0 -> Quad("Percentage (%)", "Enter percentage, e.g. 20", "Value", "Enter value, e.g. 500")
                    1 -> Quad("Part", "The smaller amount, e.g. 30", "Whole", "The total amount, e.g. 200")
                    2 -> Quad("Original Value", "Value before change, e.g. 100", "New Value", "Value after change, e.g. 125")
                    3 -> Quad("Value 1", "First number, e.g. 50", "Value 2", "Second number, e.g. 70")
                    else -> Quad("Final Value", "Amount after percentage, e.g. 120", "Percentage Added (%)", "E.g. 20 (find base 100)")
                }

                OutlinedTextField(
                    value = input1,
                    onValueChange = { input1 = it; committedResult = null },
                    label = { Text(label1) },
                    placeholder = { Text(placeholder1) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.Percent,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = input2,
                    onValueChange = { input2 = it; committedResult = null },
                    label = { Text(label2) },
                    placeholder = { Text(placeholder2) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { calculate() }),
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            input1 = ""
                            input2 = ""
                            committedResult = null
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Clear")
                    }

                    Button(
                        onClick = { calculate() },
                        modifier = Modifier.weight(2f),
                    ) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Calculate")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result card
        val isPositiveChange = committedResult?.isPositive
        val resultColor = when {
            mode == 2 && isPositiveChange == true -> Color(0xFF4CAF50)
            mode == 2 && isPositiveChange == false -> Color(0xFFEF5350)
            else -> MaterialTheme.colorScheme.primary
        }
        val resultContainerColor = when {
            mode == 2 && isPositiveChange == true -> Color(0xFF4CAF50).copy(alpha = 0.12f)
            mode == 2 && isPositiveChange == false -> Color(0xFFEF5350).copy(alpha = 0.12f)
            else -> MaterialTheme.colorScheme.primaryContainer
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = resultContainerColor),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Result",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (mode == 2 && committedResult != null) {
                            Icon(
                                imageVector = when (isPositiveChange) {
                                    true -> Icons.AutoMirrored.Filled.TrendingUp
                                    false -> Icons.AutoMirrored.Filled.TrendingDown
                                    else -> Icons.AutoMirrored.Filled.TrendingFlat
                                },
                                contentDescription = null,
                                tint = resultColor,
                            )
                        }
                        if (committedResult != null) {
                            IconButton(
                                onClick = { copyToClipboard(context, "Percentage Result", committedResult!!.value) },
                                modifier = Modifier.size(28.dp),
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy result",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (committedResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "= ${committedResult!!.value}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = resultColor,
                    )

                    val desc = committedResult?.description
                    if (desc != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Formula",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (mode) {
                        0 -> "result = (percentage ÷ 100) × value"
                        1 -> "result = (part ÷ whole) × 100"
                        2 -> "result = ((new − original) ÷ original) × 100"
                        3 -> "result = |V1 − V2| ÷ ((V1 + V2) ÷ 2) × 100"
                        else -> "original = final ÷ (1 + percentage ÷ 100)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private data class Quad(val a: String, val b: String, val c: String, val d: String)

data class PercentResult(
    val value: String,
    val description: String? = null,
    val isPositive: Boolean? = null,
)

fun calculatePercentResult(mode: Int, input1: String, input2: String): PercentResult? {
    val a = input1.toDoubleOrNull() ?: return null
    val b = input2.toDoubleOrNull() ?: return null

    return when (mode) {
        0 -> {
            val r = (a / 100.0) * b
            PercentResult(
                value = fmtPercent(r),
                description = "${fmtPercent(a)}% of ${fmtPercent(b)} = ${fmtPercent(r)}",
            )
        }
        1 -> {
            if (b == 0.0) return PercentResult("Error", "Whole cannot be zero")
            val r = (a / b) * 100.0
            PercentResult(
                value = "${fmtPercent(r)}%",
                description = "${fmtPercent(a)} is ${fmtPercent(r)}% of ${fmtPercent(b)}",
            )
        }
        2 -> {
            if (a == 0.0) return PercentResult("Error", "Original value cannot be zero")
            val r = ((b - a) / a) * 100.0
            val positive = r >= 0
            PercentResult(
                value = "${if (positive) "+" else ""}${fmtPercent(r)}%",
                description = if (positive) "increase from ${fmtPercent(a)} to ${fmtPercent(b)}"
                              else "decrease from ${fmtPercent(a)} to ${fmtPercent(b)}",
                isPositive = positive,
            )
        }
        3 -> {
            val average = (a + b) / 2.0
            if (average == 0.0) return PercentResult("Error", "Average cannot be zero")
            val r = (abs(a - b) / average) * 100.0
            PercentResult(
                value = "${fmtPercent(r)}%",
                description = "Difference between ${fmtPercent(a)} and ${fmtPercent(b)} is ${fmtPercent(r)}%",
            )
        }
        4 -> {
            if (1.0 + (b / 100.0) == 0.0) return PercentResult("Error", "Invalid percentage rate")
            val original = a / (1.0 + (b / 100.0))
            PercentResult(
                value = fmtPercent(original),
                description = "Original value before ${fmtPercent(b)}% addition = ${fmtPercent(original)}",
            )
        }
        else -> null
    }
}

private fun fmtPercent(value: Double): String =
    if (value == floor(value) && !value.isInfinite() && abs(value) < 1e12) value.toLong().toString()
    else "%.4g".format(value)
