package com.toolbox.conversion.discount

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
import androidx.compose.material.icons.filled.LocalOffer
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
import java.text.NumberFormat
import java.util.Locale

enum class DiscountMode(val title: String) {
    Single("Single / Tax"),
    Successive("Successive"),
    Compare("Compare Deals"),
}

data class DiscountResult(
    val originalPrice: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val finalPrice: Double,
    val totalSavingsPercent: Double,
)

data class ComparisonResult(
    val deal1Final: Double,
    val deal1Savings: Double,
    val deal2Final: Double,
    val deal2Savings: Double,
    val betterDeal: String,
    val difference: Double,
)

fun calculateDiscount(
    originalPrice: Double,
    discount1: Double,
    discount2: Double = 0.0,
    taxPercent: Double = 0.0,
): DiscountResult {
    val afterDisc1 = originalPrice * (1.0 - (discount1.coerceAtLeast(0.0) / 100.0))
    val afterDisc2 = afterDisc1 * (1.0 - (discount2.coerceAtLeast(0.0) / 100.0))
    val totalDiscountAmount = (originalPrice - afterDisc2).coerceAtLeast(0.0)
    val taxAmount = afterDisc2 * (taxPercent.coerceAtLeast(0.0) / 100.0)
    val finalPrice = (afterDisc2 + taxAmount).coerceAtLeast(0.0)
    val totalSavingsPercent = if (originalPrice > 0) {
        ((originalPrice - (afterDisc2)) / originalPrice) * 100.0
    } else 0.0

    return DiscountResult(
        originalPrice = originalPrice,
        discountAmount = totalDiscountAmount,
        taxAmount = taxAmount,
        finalPrice = finalPrice,
        totalSavingsPercent = totalSavingsPercent,
    )
}

fun compareDeals(
    price1: Double,
    discount1: Double,
    price2: Double,
    discount2: Double,
): ComparisonResult {
    val final1 = price1 * (1.0 - (discount1 / 100.0))
    val savings1 = price1 - final1
    val final2 = price2 * (1.0 - (discount2 / 100.0))
    val savings2 = price2 - final2

    val betterDeal = when {
        final1 < final2 -> "Deal A is cheaper by"
        final2 < final1 -> "Deal B is cheaper by"
        else -> "Both deals cost the same"
    }

    return ComparisonResult(
        deal1Final = final1,
        deal1Savings = savings1,
        deal2Final = final2,
        deal2Savings = savings2,
        betterDeal = betterDeal,
        difference = kotlin.math.abs(final1 - final2),
    )
}

private val moneyFmt: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

@Composable
fun DiscountCalculatorScreen() {
    var mode by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Inputs for single/successive
    var priceInput by rememberSaveable { mutableStateOf("") }
    var disc1Input by rememberSaveable { mutableStateOf("") }
    var disc2Input by rememberSaveable { mutableStateOf("") }
    var taxInput by rememberSaveable { mutableStateOf("") }

    // Inputs for comparison
    var deal1Price by rememberSaveable { mutableStateOf("") }
    var deal1Disc by rememberSaveable { mutableStateOf("") }
    var deal2Price by rememberSaveable { mutableStateOf("") }
    var deal2Disc by rememberSaveable { mutableStateOf("") }

    val quickDiscounts = listOf(5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 50.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DiscountMode.entries.forEachIndexed { index, m ->
                FilterChip(
                    selected = mode == index,
                    onClick = { mode = index },
                    label = { Text(m.title) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mode == 0 || mode == 1) {
            // Price & Discount Inputs
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Original Price") },
                        placeholder = { Text("e.g. 1000") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = disc1Input,
                        onValueChange = { disc1Input = it },
                        label = { Text(if (mode == 1) "First Discount (%)" else "Discount (%)") },
                        placeholder = { Text("e.g. 20") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                        singleLine = true,
                        trailingIcon = {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                    )

                    // Quick percentage chips
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        quickDiscounts.take(4).forEach { q ->
                            FilterChip(
                                selected = disc1Input == q.toInt().toString(),
                                onClick = { disc1Input = q.toInt().toString() },
                                label = { Text("${q.toInt()}%", fontSize = 12.sp) },
                            )
                        }
                    }

                    if (mode == 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = disc2Input,
                            onValueChange = { disc2Input = it },
                            label = { Text("Second Discount (%)") },
                            placeholder = { Text("e.g. 10 (applied to remaining)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            singleLine = true,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = taxInput,
                        onValueChange = { taxInput = it },
                        label = { Text("Tax / VAT after discount (%) [Optional]") },
                        placeholder = { Text("e.g. 5, 12, 18") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            priceInput = ""
                            disc1Input = ""
                            disc2Input = ""
                            taxInput = ""
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Clear Inputs")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val price = priceInput.toDoubleOrNull() ?: 0.0
            val disc1 = disc1Input.toDoubleOrNull() ?: 0.0
            val disc2 = if (mode == 1) (disc2Input.toDoubleOrNull() ?: 0.0) else 0.0
            val tax = taxInput.toDoubleOrNull() ?: 0.0

            if (price > 0.0) {
                val res = calculateDiscount(price, disc1, disc2, tax)

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
                            Text("Final Price", style = MaterialTheme.typography.labelMedium)
                            IconButton(
                                onClick = { copyToClipboard(context, "Final Price", moneyFmt.format(res.finalPrice)) },
                                modifier = Modifier.size(28.dp),
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = moneyFmt.format(res.finalPrice),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You save: ${moneyFmt.format(res.discountAmount)} (${moneyFmt.format(res.totalSavingsPercent)}%)",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        BreakdownRow("Original Price", moneyFmt.format(res.originalPrice))
                        BreakdownRow("Total Discount", "- ${moneyFmt.format(res.discountAmount)}")
                        if (res.taxAmount > 0.0) {
                            BreakdownRow("Tax (${tax}%)", "+ ${moneyFmt.format(res.taxAmount)}")
                        }
                    }
                }
            }
        } else {
            // Compare 2 Deals
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Deal A", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = deal1Price,
                            onValueChange = { deal1Price = it },
                            label = { Text("Price A") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = deal1Disc,
                            onValueChange = { deal1Disc = it },
                            label = { Text("Discount A (%)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Deal B", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = deal2Price,
                            onValueChange = { deal2Price = it },
                            label = { Text("Price B") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = deal2Disc,
                            onValueChange = { deal2Disc = it },
                            label = { Text("Discount B (%)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val p1 = deal1Price.toDoubleOrNull() ?: 0.0
            val d1 = deal1Disc.toDoubleOrNull() ?: 0.0
            val p2 = deal2Price.toDoubleOrNull() ?: 0.0
            val d2 = deal2Disc.toDoubleOrNull() ?: 0.0

            if (p1 > 0.0 && p2 > 0.0) {
                val cmp = compareDeals(p1, d1, p2, d2)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Comparison Result", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (cmp.difference > 0) "${cmp.betterDeal} ${moneyFmt.format(cmp.difference)}" else cmp.betterDeal,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        BreakdownRow("Deal A Final Price", moneyFmt.format(cmp.deal1Final))
                        BreakdownRow("Deal A Savings", moneyFmt.format(cmp.deal1Savings))
                        Spacer(modifier = Modifier.height(6.dp))
                        BreakdownRow("Deal B Final Price", moneyFmt.format(cmp.deal2Final))
                        BreakdownRow("Deal B Savings", moneyFmt.format(cmp.deal2Savings))
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
