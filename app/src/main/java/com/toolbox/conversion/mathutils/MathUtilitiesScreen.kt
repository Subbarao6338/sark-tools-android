package com.toolbox.conversion.mathutils

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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.copyToClipboard
import java.math.BigInteger
import kotlin.random.Random

// ----------------- Math Engines -----------------

data class PrimeCheckResult(
    val isPrime: Boolean,
    val factors: List<Long>,
    val nextPrime: Long,
    val prevPrime: Long?,
)

fun checkPrime(n: Long): PrimeCheckResult {
    if (n <= 1) {
        return PrimeCheckResult(false, emptyList(), 2, null)
    }
    val factors = mutableListOf<Long>()
    var temp = n
    var d = 2L
    while (d * d <= temp) {
        while (temp % d == 0L) {
            factors.add(d)
            temp /= d
        }
        d++
    }
    if (temp > 1) factors.add(temp)

    val isPrime = (factors.size == 1 && factors[0] == n)

    // Next prime
    var next = n + 1
    while (!isProbablePrime(next)) next++

    // Previous prime
    var prev: Long? = null
    if (n > 2) {
        var p = n - 1
        while (p >= 2 && !isProbablePrime(p)) p--
        if (p >= 2) prev = p
    }

    return PrimeCheckResult(isPrime, factors, next, prev)
}

private fun isProbablePrime(num: Long): Boolean {
    if (num <= 1) return false
    if (num <= 3) return true
    if (num % 2 == 0L || num % 3 == 0L) return false
    var i = 5L
    while (i * i <= num) {
        if (num % i == 0L || num % (i + 2) == 0L) return false
        i += 6
    }
    return true
}

fun calculateBigFactorial(n: Int): BigInteger {
    if (n < 0) return BigInteger.ZERO
    if (n <= 1) return BigInteger.ONE
    var result = BigInteger.ONE
    for (i in 2..n) {
        result = result.multiply(BigInteger.valueOf(i.toLong()))
    }
    return result
}

fun gcd(a: Long, b: Long): Long {
    var x = kotlin.math.abs(a)
    var y = kotlin.math.abs(b)
    while (y != 0L) {
        val temp = y
        y = x % y
        x = temp
    }
    return x
}

fun lcm(a: Long, b: Long): Long {
    if (a == 0L || b == 0L) return 0L
    val g = gcd(a, b)
    return kotlin.math.abs(a / g * b)
}

fun calculateMultiGcdLcm(numbers: List<Long>): Pair<Long, Long>? {
    if (numbers.isEmpty()) return null
    var currentGcd = numbers[0]
    var currentLcm = numbers[0]
    for (i in 1 until numbers.size) {
        currentGcd = gcd(currentGcd, numbers[i])
        currentLcm = lcm(currentLcm, numbers[i])
    }
    return Pair(currentGcd, currentLcm)
}

data class StatisticsResult(
    val count: Int,
    val sum: Double,
    val mean: Double,
    val median: Double,
    val mode: List<Double>,
    val min: Double,
    val max: Double,
)

fun calculateStatistics(numbers: List<Double>): StatisticsResult? {
    if (numbers.isEmpty()) return null
    val count = numbers.size
    val sum = numbers.sum()
    val mean = sum / count
    val sorted = numbers.sorted()
    val median = if (count % 2 == 1) {
        sorted[count / 2]
    } else {
        (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
    }

    val freq = numbers.groupingBy { it }.eachCount()
    val maxFreq = freq.values.maxOrNull() ?: 1
    val mode = if (maxFreq > 1) {
        freq.filter { it.value == maxFreq }.keys.toList()
    } else emptyList()

    return StatisticsResult(
        count = count,
        sum = sum,
        mean = mean,
        median = median,
        mode = mode,
        min = sorted.first(),
        max = sorted.last(),
    )
}

// ----------------- UI Screen -----------------

@Composable
fun MathUtilitiesScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Prime", "Factorial", "LCM & GCD", "Statistics", "Random")

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            when (selectedTab) {
                0 -> PrimeCheckerTab()
                1 -> FactorialTab()
                2 -> LcmGcdTab()
                3 -> StatisticsTab()
                4 -> RandomGeneratorTab()
            }
        }
    }
}

@Composable
private fun PrimeCheckerTab() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("") }
    val num = input.toLongOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Prime Number Checker", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() } },
                label = { Text("Enter a positive integer") },
                placeholder = { Text("e.g. 101") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )
        }
    }

    if (num != null && num > 0) {
        val res = checkPrime(num)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Result", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (res.isPrime) "$num is a PRIME number" else "$num is NOT a prime number",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                MathRow("Prime Factors", res.factors.joinToString(" × "))
                MathRow("Next Prime", "${res.nextPrime}")
                if (res.prevPrime != null) {
                    MathRow("Previous Prime", "${res.prevPrime}")
                }
            }
        }
    }
}

@Composable
private fun FactorialTab() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("") }
    val n = input.toIntOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Factorial Calculator (n!)", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() }.take(4) },
                label = { Text("Enter an integer n (0 - 500)") },
                placeholder = { Text("e.g. 25") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
            )
        }
    }

    if (n != null && n in 0..500) {
        val result = calculateBigFactorial(n)
        val resultStr = result.toString()
        val digits = resultStr.length

        Spacer(modifier = Modifier.height(16.dp))
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
                    Text("$n!", style = MaterialTheme.typography.labelMedium)
                    IconButton(
                        onClick = { copyToClipboard(context, "Factorial", resultStr) },
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (digits > 100) "${resultStr.take(100)}... ($digits digits)" else resultStr,
                    fontSize = if (digits < 20) 28.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                MathRow("Total Digits", "$digits")
            }
        }
    }
}

@Composable
private fun LcmGcdTab() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Least Common Multiple & Greatest Common Divisor", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Enter numbers separated by space or comma") },
                placeholder = { Text("e.g. 24, 36, 60") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
    }

    val nums = input.split(',', ' ', ';')
        .mapNotNull { it.trim().toLongOrNull() }
        .filter { it > 0 }

    if (nums.size >= 2) {
        val res = calculateMultiGcdLcm(nums)
        if (res != null) {
            val (gcdVal, lcmVal) = res
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Calculated for: ${nums.joinToString(", ")}", style = MaterialTheme.typography.labelMedium)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    MathRow("Greatest Common Divisor (GCD / HCF)", "$gcdVal", isBold = true)
                    MathRow("Least Common Multiple (LCM)", "$lcmVal", isBold = true)
                }
            }
        }
    }
}

@Composable
private fun StatisticsTab() {
    val context = LocalContext.current
    var input by rememberSaveable { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Average & Descriptive Statistics", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Enter dataset numbers (comma/space separated)") },
                placeholder = { Text("e.g. 10, 20, 20, 40, 50") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
    }

    val nums = input.split(',', ' ', ';')
        .mapNotNull { it.trim().toDoubleOrNull() }

    if (nums.isNotEmpty()) {
        val stats = calculateStatistics(nums)
        if (stats != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Mean / Average", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "%.4f".format(stats.mean).trimEnd('0').trimEnd('.'),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    MathRow("Count (N)", "${stats.count}")
                    MathRow("Sum", "%.4f".format(stats.sum).trimEnd('0').trimEnd('.'))
                    MathRow("Median", "%.4f".format(stats.median).trimEnd('0').trimEnd('.'))
                    MathRow("Mode", if (stats.mode.isNotEmpty()) stats.mode.joinToString(", ") else "None")
                    MathRow("Minimum", "${stats.min}")
                    MathRow("Maximum", "${stats.max}")
                }
            }
        }
    }
}

@Composable
private fun RandomGeneratorTab() {
    val context = LocalContext.current
    var minInput by rememberSaveable { mutableStateOf("1") }
    var maxInput by rememberSaveable { mutableStateOf("100") }
    var countInput by rememberSaveable { mutableStateOf("1") }
    var allowDuplicates by rememberSaveable { mutableStateOf(true) }
    var generatedList by rememberSaveable { mutableStateOf<List<Long>>(emptyList()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Random Number Generator", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = minInput,
                    onValueChange = { minInput = it },
                    label = { Text("Min") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = maxInput,
                    onValueChange = { maxInput = it },
                    label = { Text("Max") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = countInput,
                    onValueChange = { countInput = it },
                    label = { Text("Count") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = allowDuplicates,
                    onClick = { allowDuplicates = true },
                    label = { Text("Allow Duplicates") },
                )
                FilterChip(
                    selected = !allowDuplicates,
                    onClick = { allowDuplicates = false },
                    label = { Text("Unique Numbers") },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val min = minInput.toLongOrNull() ?: 1L
                    val max = maxInput.toLongOrNull() ?: 100L
                    val count = (countInput.toIntOrNull() ?: 1).coerceIn(1, 1000)
                    if (min <= max) {
                        generatedList = if (allowDuplicates) {
                            List(count) { Random.nextLong(min, max + 1) }
                        } else {
                            val rangeSize = (max - min + 1)
                            if (rangeSize >= count) {
                                val set = mutableSetOf<Long>()
                                while (set.size < count) {
                                    set.add(Random.nextLong(min, max + 1))
                                }
                                set.toList()
                            } else {
                                (min..max).shuffled().toList()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text("Generate Random Numbers")
            }
        }
    }

    if (generatedList.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
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
                    Text("Generated (${generatedList.size})", style = MaterialTheme.typography.labelMedium)
                    IconButton(
                        onClick = { copyToClipboard(context, "Random Numbers", generatedList.joinToString(", ")) },
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = generatedList.joinToString(", "),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun MathRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold)
    }
}
