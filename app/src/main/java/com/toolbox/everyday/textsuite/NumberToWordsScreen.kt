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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.copyToClipboard

@Composable
fun NumberToWordsScreen() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("1254300") }
    var useIndianSystem by rememberSaveable { mutableStateOf(true) }

    val resultEnglish = convertNumberToWords(input, useIndianSystem)
    val resultHindi = convertNumberToHindiWords(input, useIndianSystem)

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
                Text("Enter Number", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
                    label = { Text("Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = useIndianSystem,
                        onClick = { useIndianSystem = true },
                        label = { Text("Indian (Lakh/Crore)") },
                    )
                    FilterChip(
                        selected = !useIndianSystem,
                        onClick = { useIndianSystem = false },
                        label = { Text("International (Million/Billion)") },
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("English Words", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    text = resultEnglish,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                OutlinedButton(onClick = { copyToClipboard(context, "Number in Words", resultEnglish) }) {
                    Text("Copy English")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Hindi Transliteration", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text(
                    text = resultHindi,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                OutlinedButton(onClick = { copyToClipboard(context, "Hindi Words", resultHindi) }) {
                    Text("Copy Hindi")
                }
            }
        }
    }
}

private val ONES = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
    "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
private val TENS = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

fun convertNumberToWords(numStr: String, indian: Boolean): String {
    if (numStr.isBlank()) return ""
    val isNeg = numStr.startsWith("-")
    val clean = if (isNeg) numStr.substring(1) else numStr
    val parts = clean.split(".")
    val intPart = parts[0].toLongOrNull() ?: return "Number out of range"
    if (intPart == 0L && parts.size == 1) return "Zero"

    fun convertUnder1000(n: Long): String {
        var str = ""
        if (n >= 100) {
            str += ONES[(n / 100).toInt()] + " Hundred "
        }
        val rem = n % 100
        if (rem in 1..19) {
            str += ONES[rem.toInt()] + " "
        } else if (rem >= 20) {
            str += TENS[(rem / 10).toInt()] + " " + ONES[(rem % 10).toInt()] + " "
        }
        return str.trim()
    }

    var result = ""
    var n = intPart

    if (indian) {
        if (n >= 10_000_000) {
            result += convertNumberToWords((n / 10_000_000).toString(), true) + " Crore "
            n %= 10_000_000
        }
        if (n >= 100_000) {
            result += convertUnder1000(n / 100_000) + " Lakh "
            n %= 100_000
        }
        if (n >= 1000) {
            result += convertUnder1000(n / 1000) + " Thousand "
            n %= 1000
        }
        if (n > 0) {
            result += convertUnder1000(n)
        }
    } else {
        if (n >= 1_000_000_000) {
            result += convertUnder1000(n / 1_000_000_000) + " Billion "
            n %= 1_000_000_000
        }
        if (n >= 1_000_000) {
            result += convertUnder1000(n / 1_000_000) + " Million "
            n %= 1_000_000
        }
        if (n >= 1000) {
            result += convertUnder1000(n / 1000) + " Thousand "
            n %= 1000
        }
        if (n > 0) {
            result += convertUnder1000(n)
        }
    }

    result = result.trim()
    if (result.isEmpty()) result = "Zero"
    if (isNeg) result = "Negative $result"

    if (parts.size > 1 && parts[1].isNotEmpty()) {
        result += " point " + parts[1].map { ONES[it - '0'] }.joinToString(" ")
    }
    return result
}

fun convertNumberToHindiWords(numStr: String, indian: Boolean): String {
    val eng = convertNumberToWords(numStr, indian)
    return eng.replace("Thousand", "Hazaar")
        .replace("Hundred", "Sau")
        .replace("Zero", "Shunya")
        .replace("Negative", "Rinaatmak")
        .replace("point", "dashamlav")
}
