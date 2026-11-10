package com.toolbox.programmingtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BaseConverterProScreen() {
    var decInput by remember { mutableStateOf("42") }
    var binOutput by remember { mutableStateOf("") }
    var hexOutput by remember { mutableStateOf("") }
    var octOutput by remember { mutableStateOf("") }
    var ieee754Output by remember { mutableStateOf("") }

    fun convert() {
        val n = decInput.toLongOrNull() ?: 0L
        binOutput = java.lang.Long.toBinaryString(n)
        hexOutput = java.lang.Long.toHexString(n).uppercase()
        octOutput = java.lang.Long.toOctalString(n)

        val f = decInput.toFloatOrNull() ?: 0f
        val bits = java.lang.Float.floatToIntBits(f)
        ieee754Output = Integer.toBinaryString(bits).padStart(32, '0')
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Base & IEEE-754 Binary Inspector", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Inspect 64-bit integer bit representations and 32-bit single-precision IEEE-754 floating point bits.")
            }
        }

        OutlinedTextField(
            value = decInput,
            onValueChange = { decInput = it },
            label = { Text("Decimal Value") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { convert() }, modifier = Modifier.fillMaxWidth()) {
            Text("Compute Binary & Bit Layout")
        }

        if (binOutput.isNotBlank()) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Binary (Base 2):", fontWeight = FontWeight.Bold)
                    Text(binOutput, fontFamily = FontFamily.Monospace)

                    Text("Hexadecimal (Base 16):", fontWeight = FontWeight.Bold)
                    Text("0x$hexOutput", fontFamily = FontFamily.Monospace)

                    Text("Octal (Base 8):", fontWeight = FontWeight.Bold)
                    Text("0o$octOutput", fontFamily = FontFamily.Monospace)

                    Text("IEEE-754 Float32 Bits (Sign | Exp | Mantissa):", fontWeight = FontWeight.Bold)
                    val sign = ieee754Output.take(1)
                    val exp = ieee754Output.drop(1).take(8)
                    val mantissa = ieee754Output.drop(9)
                    Text("$sign | $exp | $mantissa", fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
