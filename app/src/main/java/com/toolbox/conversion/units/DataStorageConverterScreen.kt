package com.toolbox.conversion.units

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class DataStorageUnit(val id: String, val name: String, val symbol: String, val toBytes: Double)

val dataStorageUnits = listOf(
    DataStorageUnit("b", "Byte", "B", 1.0),
    DataStorageUnit("kb", "Kilobyte (decimal)", "KB", 1000.0),
    DataStorageUnit("mb", "Megabyte (decimal)", "MB", 1_000_000.0),
    DataStorageUnit("gb", "Gigabyte (decimal)", "GB", 1_000_000_000.0),
    DataStorageUnit("tb", "Terabyte (decimal)", "TB", 1_000_000_000_000.0),
    DataStorageUnit("kib", "Kibibyte (binary)", "KiB", 1024.0),
    DataStorageUnit("mib", "Mebibyte (binary)", "MiB", 1048576.0),
    DataStorageUnit("gib", "Gibibyte (binary)", "GiB", 1073741824.0),
    DataStorageUnit("tib", "Tebibyte (binary)", "TiB", 1099511627776.0),
)

object DataStorageConverterEngine {
    fun convert(value: Double, from: DataStorageUnit, to: DataStorageUnit): Double {
        val bytes = value * from.toBytes
        return bytes / to.toBytes
    }

    fun format(value: Double): String {
        return if (value == 0.0) "0"
        else if (value in 0.0001..999999.0) String.format("%.4f", value).trimEnd('0').trimEnd('.')
        else String.format("%.4e", value)
    }
}

@Composable
fun DataStorageConverterScreen() {
    var inputValue by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf(dataStorageUnits[3]) } // Default 1 GB

    val numericInput = inputValue.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Convert From: ${selectedUnit.name} (${selectedUnit.symbol})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Text("All Storage Units (Decimal vs Binary)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(dataStorageUnits) { unit ->
                val isSelected = unit == selectedUnit
                val converted = DataStorageConverterEngine.convert(numericInput, selectedUnit, unit)
                val formatted = DataStorageConverterEngine.format(converted)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedUnit = unit },
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = unit.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formatted,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = unit.symbol,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
