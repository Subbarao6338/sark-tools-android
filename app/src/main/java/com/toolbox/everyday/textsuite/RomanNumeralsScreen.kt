package com.toolbox.everyday.textsuite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun RomanNumeralsScreen() {
    val context = LocalContext.current
    var arabicInput by rememberSaveable { mutableStateOf("2024") }
    var romanInput by rememberSaveable { mutableStateOf("MMXXIV") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Arabic to Roman (1 - 3999)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = arabicInput,
                    onValueChange = {
                        arabicInput = it.filter { c -> c.isDigit() }
                        val num = arabicInput.toIntOrNull()
                        if (num != null && num in 1..3999) {
                            romanInput = toRoman(num)
                        }
                    },
                    label = { Text("Number (e.g. 2024)") },
                    modifier = Modifier.fillMaxWidth(),
                )

                val romanResult = arabicInput.toIntOrNull()?.let { toRoman(it) } ?: "Enter number 1-3999"
                Text(
                    text = "Roman: $romanResult",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                OutlinedButton(onClick = { copyToClipboard(context, "Roman Numeral", romanResult) }) {
                    Text("Copy Roman")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Roman to Arabic", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = romanInput,
                    onValueChange = {
                        romanInput = it.uppercase().filter { c -> c in "IVXLCDM" }
                        val arabic = fromRoman(romanInput)
                        if (arabic != null) {
                            arabicInput = arabic.toString()
                        }
                    },
                    label = { Text("Roman (e.g. MMXXIV)") },
                    modifier = Modifier.fillMaxWidth(),
                )

                val arabicResult = fromRoman(romanInput)?.toString() ?: "Invalid Roman Numeral"
                Text(
                    text = "Arabic: $arabicResult",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
                OutlinedButton(onClick = { copyToClipboard(context, "Arabic Number", arabicResult) }) {
                    Text("Copy Arabic")
                }
            }
        }
    }
}

private val ROMAN_VALS = listOf(
    1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
    100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
    10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
)

fun toRoman(num: Int): String {
    if (num <= 0 || num > 3999) return "Out of range (1-3999)"
    var rem = num
    val sb = StringBuilder()
    for ((v, s) in ROMAN_VALS) {
        while (rem >= v) {
            sb.append(s)
            rem -= v
        }
    }
    return sb.toString()
}

fun fromRoman(roman: String): Int? {
    if (roman.isBlank()) return null
    val map = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
    var total = 0
    var prev = 0
    for (i in roman.length - 1 downTo 0) {
        val curr = map[roman[i]] ?: return null
        if (curr < prev) {
            total -= curr
        } else {
            total += curr
        }
        prev = curr
    }
    if (toRoman(total) != roman) return null
    return total
}
