package com.toolbox.conversion.gst

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.copyToClipboard
import java.text.NumberFormat
import java.util.Locale

data class GstResult(
    val baseAmount: Double,
    val gstRate: Double,
    val gstAmount: Double,
    val finalAmount: Double,
    val isInclusive: Boolean,
    val isInterState: Boolean,
    val cgst: Double,
    val sgst: Double,
    val igst: Double,
)

fun calculateGst(
    amount: Double,
    rate: Double,
    isInclusive: Boolean,
    isInterState: Boolean,
): GstResult {
    val r = rate.coerceAtLeast(0.0)
    val baseAmount: Double
    val gstAmount: Double
    val finalAmount: Double

    if (isInclusive) {
        // Base = Inclusive * 100 / (100 + Rate)
        baseAmount = if (100.0 + r != 0.0) (amount * 100.0) / (100.0 + r) else amount
        gstAmount = amount - baseAmount
        finalAmount = amount
    } else {
        // Exclusive: GST = Base * Rate / 100
        baseAmount = amount
        gstAmount = (baseAmount * r) / 100.0
        finalAmount = baseAmount + gstAmount
    }

    val cgst = if (!isInterState) gstAmount / 2.0 else 0.0
    val sgst = if (!isInterState) gstAmount / 2.0 else 0.0
    val igst = if (isInterState) gstAmount else 0.0

    return GstResult(
        baseAmount = baseAmount,
        gstRate = r,
        gstAmount = gstAmount,
        finalAmount = finalAmount,
        isInclusive = isInclusive,
        isInterState = isInterState,
        cgst = cgst,
        sgst = sgst,
        igst = igst,
    )
}

private val moneyFmt: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

@Composable
fun GstCalculatorScreen() {
    var isInclusive by rememberSaveable { mutableStateOf(false) }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var selectedRateIndex by rememberSaveable { mutableIntStateOf(3) } // default 18%
    var customRateInput by rememberSaveable { mutableStateOf("") }
    var isInterState by rememberSaveable { mutableStateOf(false) }

    val standardRates = listOf(0.0, 5.0, 12.0, 18.0, 28.0)
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val effectiveRate = if (selectedRateIndex < standardRates.size) {
        standardRates[selectedRateIndex]
    } else {
        customRateInput.toDoubleOrNull() ?: 0.0
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Exclusive vs Inclusive Toggle
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = !isInclusive,
                onClick = { isInclusive = false },
                shape = SegmentedButtonDefaults.itemShape(0, 2),
            ) {
                Text("GST Exclusive (+)")
            }
            SegmentedButton(
                selected = isInclusive,
                onClick = { isInclusive = true },
                shape = SegmentedButtonDefaults.itemShape(1, 2),
            ) {
                Text("GST Inclusive (Reverse)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text(if (isInclusive) "Total Invoice Amount (Inclusive)" else "Base Amount (Exclusive)") },
                    placeholder = { Text("e.g. 5000") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("GST Rate", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Rate Chips (0%, 5%, 12%, 18%, 28%, Custom)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    standardRates.forEachIndexed { idx, rate ->
                        FilterChip(
                            selected = selectedRateIndex == idx,
                            onClick = { selectedRateIndex = idx },
                            label = { Text("${rate.toInt()}%", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    FilterChip(
                        selected = selectedRateIndex == standardRates.size,
                        onClick = { selectedRateIndex = standardRates.size },
                        label = { Text("Custom", fontSize = 13.sp) },
                        modifier = Modifier.weight(1.2f),
                    )
                }

                if (selectedRateIndex == standardRates.size) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customRateInput,
                        onValueChange = { customRateInput = it },
                        label = { Text("Custom GST Rate (%)") },
                        placeholder = { Text("e.g. 7.5") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Inter-state toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Inter-state Transaction (IGST)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            if (isInterState) "IGST applied (Interstate)" else "CGST + SGST applied (Intrastate)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = isInterState, onCheckedChange = { isInterState = it })
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        amountInput = ""
                        customRateInput = ""
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Clear")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result Breakdown Card
        if (amount > 0.0) {
            val res = calculateGst(amount, effectiveRate, isInclusive, isInterState)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            if (isInclusive) "Base Amount (Net)" else "Total Amount (Gross)",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        IconButton(
                            onClick = {
                                copyToClipboard(
                                    context,
                                    "GST Calculation",
                                    "Total: ${moneyFmt.format(res.finalAmount)} | GST: ${moneyFmt.format(res.gstAmount)}",
                                )
                            },
                            modifier = Modifier.size(28.dp),
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        }
                    }

                    Text(
                        text = moneyFmt.format(res.finalAmount),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total Tax: ${moneyFmt.format(res.gstAmount)} (${res.gstRate}%)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    GstRow("Base Net Amount", moneyFmt.format(res.baseAmount))
                    GstRow("GST Rate", "${res.gstRate}%")
                    GstRow("Total GST Amount", moneyFmt.format(res.gstAmount))

                    if (isInterState) {
                        GstRow("IGST (${res.gstRate}%)", moneyFmt.format(res.igst))
                    } else {
                        val halfRate = res.gstRate / 2.0
                        GstRow("CGST ($halfRate%)", moneyFmt.format(res.cgst))
                        GstRow("SGST ($halfRate%)", moneyFmt.format(res.sgst))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    GstRow("Final Total Amount", moneyFmt.format(res.finalAmount), isBold = true)
                }
            }
        }
    }
}

@Composable
private fun GstRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}
