package com.toolbox.conversion.currency

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class CurrencyDef(val code: String, val name: String, val symbol: String, val rateToUsd: Double)

val standardCurrencies = listOf(
    CurrencyDef("USD", "US Dollar", "$", 1.0),
    CurrencyDef("EUR", "Euro", "€", 0.92),
    CurrencyDef("INR", "Indian Rupee", "₹", 86.5),
    CurrencyDef("GBP", "British Pound", "£", 0.78),
    CurrencyDef("JPY", "Japanese Yen", "¥", 154.2),
    CurrencyDef("CAD", "Canadian Dollar", "C$", 1.38),
    CurrencyDef("AUD", "Australian Dollar", "A$", 1.55),
    CurrencyDef("SGD", "Singapore Dollar", "S$", 1.34),
    CurrencyDef("AED", "UAE Dirham", "AED", 3.67),
    CurrencyDef("CNY", "Chinese Yuan", "¥", 7.24),
    CurrencyDef("CHF", "Swiss Franc", "CHF", 0.88),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen() {
    var fromCurrency by remember { mutableStateOf(standardCurrencies[0]) } // USD
    var toCurrency by remember { mutableStateOf(standardCurrencies[2]) }   // INR
    var fromAmount by remember { mutableStateOf("100") }
    var customExchangeRate by remember { mutableDoubleStateOf(0.0) }
    var useCustomRate by remember { mutableStateOf(false) }

    val fromVal = fromAmount.toDoubleOrNull() ?: 0.0
    val activeRate = if (useCustomRate && customExchangeRate > 0) {
        customExchangeRate
    } else {
        toCurrency.rateToUsd / fromCurrency.rateToUsd
    }
    val convertedAmount = fromVal * activeRate

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Exchange Rate Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "1 ${fromCurrency.code} = ${String.format("%.4f", activeRate)} ${toCurrency.code}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = "Offline benchmark rates with instant conversion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                }
            }
        }

        // From Currency Card
        CurrencyCard(
            label = "From Amount",
            selected = fromCurrency,
            currencies = standardCurrencies,
            onSelected = { fromCurrency = it },
            amount = fromAmount,
            onAmountChange = { fromAmount = it },
            isEditable = true,
        )

        // Swap button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = {
                    val temp = fromCurrency
                    fromCurrency = toCurrency
                    toCurrency = temp
                },
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap currencies",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // To Currency Card
        CurrencyCard(
            label = "Converted To",
            selected = toCurrency,
            currencies = standardCurrencies,
            onSelected = { toCurrency = it },
            amount = String.format("%.2f", convertedAmount),
            onAmountChange = {},
            isEditable = false,
        )

        // Offline notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Offline Reference Rates",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Currencies use built-in reference exchange rates. Completely offline without tracking or data connection required.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyCard(
    label: String,
    selected: CurrencyDef,
    currencies: List<CurrencyDef>,
    onSelected: (CurrencyDef) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    isEditable: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                OutlinedTextField(
                    value = "${selected.code} - ${selected.name} (${selected.symbol})",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = RoundedCornerShape(12.dp),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(
                            text = { Text("${curr.code} - ${curr.name} (${curr.symbol})") },
                            onClick = {
                                onSelected(curr)
                                expanded = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                readOnly = !isEditable,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(12.dp),
                prefix = {
                    Text(
                        text = selected.symbol + " ",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            )
        }
    }
}
