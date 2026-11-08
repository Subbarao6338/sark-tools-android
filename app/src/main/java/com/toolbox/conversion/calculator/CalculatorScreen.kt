package com.toolbox.conversion.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toolbox.core.ui.copyToClipboard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    onUseResult: ((String) -> Unit)? = null,
) {
    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var isScientific by rememberSaveable { mutableStateOf(false) }
    var is2ndFunction by rememberSaveable { mutableStateOf(false) }
    var showHistory by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        // Display Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.32f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Top row with history & copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { showHistory = true },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (state.result.isNotBlank() && state.result != "Error") {
                        IconButton(
                            onClick = { copyToClipboard(context, "Calculator Result", state.result) },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy result",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Expression & Live Result
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = state.expression.ifEmpty { "0" },
                        fontSize = if (state.expression.length > 18) 22.sp else 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (state.showFraction && state.fractionResult.isNotBlank()) state.fractionResult else state.result.ifEmpty { " " },
                        fontSize = if (state.result.length > 12) 36.sp else 46.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                    )
                    if (state.fractionResult.isNotBlank() && state.fractionResult != state.result) {
                        Text(
                            text = if (state.showFraction) "= ${state.result}" else "= ${state.fractionResult}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Memory & Fraction Utility Bar (MC, MR, M+, M-, Fraction toggle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalButton(
                onClick = { viewModel.memoryClear() },
                modifier = Modifier.weight(1f).height(36.dp),
                enabled = state.hasMemory,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("MC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = { viewModel.memoryRecall() },
                modifier = Modifier.weight(1f).height(36.dp),
                enabled = state.hasMemory,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("MR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = { viewModel.memoryAdd() },
                modifier = Modifier.weight(1f).height(36.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("M+", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = { viewModel.memorySubtract() },
                modifier = Modifier.weight(1f).height(36.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("M−", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = { viewModel.toggleFraction() },
                modifier = Modifier.weight(1.1f).height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (state.showFraction) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                Text("a b/c", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tool bar: Mode toggle (Basic vs Scientific) + Angle Mode (DEG/RAD) + Use Result
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isScientific,
                    onClick = { isScientific = false },
                    label = { Text("Standard") },
                )
                FilterChip(
                    selected = isScientific,
                    onClick = { isScientific = true },
                    label = { Text("Scientific") },
                    leadingIcon = { Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp)) },
                )
            }

            if (isScientific) {
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = state.useDegrees,
                        onClick = { if (!state.useDegrees) viewModel.toggleAngleMode() },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                    ) {
                        Text("DEG", fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = !state.useDegrees,
                        onClick = { if (state.useDegrees) viewModel.toggleAngleMode() },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                    ) {
                        Text("RAD", fontSize = 12.sp)
                    }
                }
            }

            if (onUseResult != null && state.result.isNotBlank() && state.result != "Error") {
                OutlinedButton(
                    onClick = { onUseResult(state.result) },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardReturn,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    Text("Use", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val onClick = { token: String ->
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.onInput(token)
        }

        // Scientific Extended Rows (when scientific mode is enabled)
        AnimatedVisibility(
            visible = isScientific,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Sci Row 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val m = Modifier.weight(1f)
                    FilterChip(
                        selected = is2ndFunction,
                        onClick = { is2ndFunction = !is2ndFunction },
                        label = { Text("2nd", fontSize = 13.sp) },
                        modifier = m,
                    )
                    SciKey(if (is2ndFunction) "asin" else "sin", m) { onClick(if (is2ndFunction) "asin(" else "sin(") }
                    SciKey(if (is2ndFunction) "acos" else "cos", m) { onClick(if (is2ndFunction) "acos(" else "cos(") }
                    SciKey(if (is2ndFunction) "atan" else "tan", m) { onClick(if (is2ndFunction) "atan(" else "tan(") }
                    SciKey("x^y", m) { onClick("^") }
                }
                // Sci Row 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val m = Modifier.weight(1f)
                    SciKey(if (is2ndFunction) "sinh" else "ln", m) { onClick(if (is2ndFunction) "sinh(" else "ln(") }
                    SciKey(if (is2ndFunction) "cosh" else "log", m) { onClick(if (is2ndFunction) "cosh(" else "log(") }
                    SciKey(if (is2ndFunction) "tanh" else "√", m) { onClick(if (is2ndFunction) "tanh(" else "sqrt(") }
                    SciKey(if (is2ndFunction) "∛" else "x!", m) { onClick(if (is2ndFunction) "cbrt(" else "!") }
                    SciKey(if (is2ndFunction) "nCr" else "nPr", m) { onClick(if (is2ndFunction) "C" else "P") }
                }
                // Sci Row 3
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val m = Modifier.weight(1f)
                    SciKey("π", m) { onClick("π") }
                    SciKey("e", m) { onClick("e") }
                    SciKey("(", m) { onClick("(") }
                    SciKey(")", m) { onClick(")") }
                    SciKey("1/x", m) { onClick("^(-1)") }
                }
            }
        }

        // Primary 4x5 Keypad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.68f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            val keyModifier = Modifier.weight(1f)

            // Row 1: AC, +/-, %, ÷
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ActionKey("AC", keyModifier, isClear = true) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.onClear()
                }
                ActionKey("+/-", keyModifier) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onNegate()
                }
                ActionKey("%", keyModifier) { onClick("%") }
                OpKey("÷", keyModifier) { onClick("÷") }
            }

            // Row 2: 7, 8, 9, ×
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NumKey("7", keyModifier) { onClick("7") }
                NumKey("8", keyModifier) { onClick("8") }
                NumKey("9", keyModifier) { onClick("9") }
                OpKey("×", keyModifier) { onClick("×") }
            }

            // Row 3: 4, 5, 6, −
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NumKey("4", keyModifier) { onClick("4") }
                NumKey("5", keyModifier) { onClick("5") }
                NumKey("6", keyModifier) { onClick("6") }
                OpKey("−", keyModifier) { onClick("-") }
            }

            // Row 4: 1, 2, 3, +
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NumKey("1", keyModifier) { onClick("1") }
                NumKey("2", keyModifier) { onClick("2") }
                NumKey("3", keyModifier) { onClick("3") }
                OpKey("+", keyModifier) { onClick("+") }
            }

            // Row 5: 0, ., ⌫, =
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NumKey("0", keyModifier) { onClick("0") }
                NumKey(".", keyModifier) { onClick(".") }
                ActionKey("⌫", keyModifier) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onBackspace()
                }
                EqualsKey("=", keyModifier) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.onEquals()
                }
            }
        }
    }

    // Calculation History Modal Bottom Sheet
    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Calculation History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (state.history.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearHistory() }) {
                            Text("Clear")
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                if (state.history.isEmpty()) {
                    Text(
                        "No calculations yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp),
                    )
                } else {
                    state.history.forEach { (expr, res) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Text(
                                text = expr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "= $res",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                TextButton(
                                    onClick = {
                                        viewModel.onHistorySelect(res)
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            showHistory = false
                                        }
                                    },
                                ) {
                                    Text("Use")
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

// ----------------- Key Components -----------------

@Composable
private fun NumKey(label: String, modifier: Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(label, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OpKey(label: String, modifier: Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Text(label, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ActionKey(label: String, modifier: Modifier, isClear: Boolean = false, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isClear) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = if (isClear) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        if (label == "⌫") {
            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace", modifier = Modifier.size(20.dp))
        } else {
            Text(label, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun EqualsKey(label: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Text(label, fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SciKey(label: String, modifier: Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
