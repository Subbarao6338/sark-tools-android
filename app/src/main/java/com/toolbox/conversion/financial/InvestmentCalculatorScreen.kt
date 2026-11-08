package com.toolbox.conversion.financial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.toolbox.core.ui.components.TileSelector
import kotlin.math.pow

@Composable
fun InvestmentCalculatorScreen() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val modes = listOf("ROI & Profit", "TVM & Compound Growth", "Savings Goal", "Fixed Mortgage")

    Column(modifier = Modifier.fillMaxSize()) {
        TileSelector(
            options = modes,
            selectedIndex = selectedIndex,
            onSelectIndex = { selectedIndex = it },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (selectedIndex) {
                0 -> RoiCalculator()
                1 -> TvmGrowthCalculator()
                2 -> SavingsGoalCalculator()
                3 -> FixedMortgageCalculator()
            }
        }
    }
}

@Composable
private fun RoiCalculator() {
    var investedStr by rememberSaveable { mutableStateOf("10000") }
    var returnedStr by rememberSaveable { mutableStateOf("14500") }
    var yearsStr by rememberSaveable { mutableStateOf("3") }

    val invested = investedStr.toDoubleOrNull() ?: 0.0
    val returned = returnedStr.toDoubleOrNull() ?: 0.0
    val years = yearsStr.toDoubleOrNull() ?: 1.0

    val profit = returned - invested
    val roiPct = if (invested > 0) (profit / invested) * 100.0 else 0.0
    val annualizedRoi = if (invested > 0 && years > 0 && returned > 0) {
        ((returned / invested).pow(1.0 / years) - 1.0) * 100.0
    } else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Return on Investment (ROI)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = investedStr, onValueChange = { investedStr = it }, label = { Text("Initial Investment ($)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = returnedStr, onValueChange = { returnedStr = it }, label = { Text("Final Value / Return ($)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = yearsStr, onValueChange = { yearsStr = it }, label = { Text("Holding Period (Years)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Investment Performance", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            Text("Net Profit / Gain: $${fmt(profit)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = if (profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            Text("Total ROI: ${fmt(roiPct)}%", fontFamily = FontFamily.Monospace)
            Text("Annualized Compound Growth (CAGR): ${fmt(annualizedRoi)}% / year", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun TvmGrowthCalculator() {
    var principalStr by rememberSaveable { mutableStateOf("5000") }
    var monthlyDepositStr by rememberSaveable { mutableStateOf("300") }
    var annualRateStr by rememberSaveable { mutableStateOf("8.0") }
    var yearsStr by rememberSaveable { mutableStateOf("10") }

    val p = principalStr.toDoubleOrNull() ?: 0.0
    val pmt = monthlyDepositStr.toDoubleOrNull() ?: 0.0
    val rate = (annualRateStr.toDoubleOrNull() ?: 0.0) / 100.0
    val years = yearsStr.toDoubleOrNull() ?: 0.0

    val n = 12.0 // monthly compounding
    val totalMonths = (years * 12).toInt()
    val monthlyRate = rate / n

    // FV = P*(1+r/n)^(nt) + PMT * [((1+r/n)^(nt) - 1) / (r/n)]
    val principalGrowth = p * (1.0 + monthlyRate).pow(totalMonths.toDouble())
    val annuityGrowth = if (monthlyRate > 0) {
        pmt * ((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate
    } else {
        pmt * totalMonths
    }
    val futureValue = principalGrowth + annuityGrowth
    val totalInvested = p + (pmt * totalMonths)
    val interestEarned = futureValue - totalInvested

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Compound Wealth Growth (TVM)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = principalStr, onValueChange = { principalStr = it }, label = { Text("Initial Deposit ($)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = monthlyDepositStr, onValueChange = { monthlyDepositStr = it }, label = { Text("Monthly Addition ($)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = annualRateStr, onValueChange = { annualRateStr = it }, label = { Text("Annual Rate (%)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = yearsStr, onValueChange = { yearsStr = it }, label = { Text("Duration (Years)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Future Portfolio Value", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            Text("$${fmt(futureValue)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text("Total Out-of-Pocket Deposits: $${fmt(totalInvested)}", fontFamily = FontFamily.Monospace)
            Text("Total Compound Interest Earned: $${fmt(interestEarned)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
            val profitMultiplier = if (totalInvested > 0) futureValue / totalInvested else 1.0
            Text("Wealth Multiplier: ${fmt(profitMultiplier)}x", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun SavingsGoalCalculator() {
    var targetStr by rememberSaveable { mutableStateOf("50000") }
    var currentStr by rememberSaveable { mutableStateOf("5000") }
    var annualRateStr by rememberSaveable { mutableStateOf("7.0") }
    var yearsStr by rememberSaveable { mutableStateOf("5") }

    val target = targetStr.toDoubleOrNull() ?: 0.0
    val current = currentStr.toDoubleOrNull() ?: 0.0
    val rate = (annualRateStr.toDoubleOrNull() ?: 0.0) / 100.0
    val years = yearsStr.toDoubleOrNull() ?: 1.0

    val totalMonths = (years * 12).toInt()
    val monthlyRate = rate / 12.0

    // FV = Target. Current grows to current * (1+r/12)^N
    val currentGrowsTo = current * (1.0 + monthlyRate).pow(totalMonths.toDouble())
    val remaining = target - currentGrowsTo

    // PMT = remaining * (r/12) / ((1+r/12)^N - 1)
    val requiredMonthly = if (remaining <= 0) 0.0 else {
        if (monthlyRate > 0) {
            remaining * monthlyRate / ((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0)
        } else {
            remaining / totalMonths
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Savings Target Goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = targetStr, onValueChange = { targetStr = it }, label = { Text("Target Goal ($)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = currentStr, onValueChange = { currentStr = it }, label = { Text("Current Balance ($)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = annualRateStr, onValueChange = { annualRateStr = it }, label = { Text("Annual Return (%)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = yearsStr, onValueChange = { yearsStr = it }, label = { Text("Timeframe (Years)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Required Monthly Contribution", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            Text("$${fmt(requiredMonthly)} / month", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            val totalContributions = requiredMonthly * totalMonths
            Text("Total New Deposits: $${fmt(totalContributions)}", fontFamily = FontFamily.Monospace)
            Text("Compound Return Lift: $${fmt(target - current - totalContributions)}", fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun FixedMortgageCalculator() {
    var homePriceStr by rememberSaveable { mutableStateOf("300000") }
    var downPaymentPctStr by rememberSaveable { mutableStateOf("20") }
    var interestRateStr by rememberSaveable { mutableStateOf("6.5") }
    var termYearsStr by rememberSaveable { mutableStateOf("30") }

    val price = homePriceStr.toDoubleOrNull() ?: 0.0
    val downPct = (downPaymentPctStr.toDoubleOrNull() ?: 0.0) / 100.0
    val rate = (interestRateStr.toDoubleOrNull() ?: 0.0) / 100.0
    val termYears = termYearsStr.toDoubleOrNull() ?: 30.0

    val downPayment = price * downPct
    val principal = price - downPayment
    val monthlyRate = rate / 12.0
    val totalMonths = (termYears * 12).toInt()

    val monthlyPayment = if (monthlyRate > 0) {
        principal * (monthlyRate * (1.0 + monthlyRate).pow(totalMonths.toDouble())) /
                ((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0)
    } else {
        principal / totalMonths
    }

    val totalPayment = monthlyPayment * totalMonths
    val totalInterest = totalPayment - principal

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Mortgage & Home Loan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = homePriceStr, onValueChange = { homePriceStr = it }, label = { Text("Home Price ($)") }, modifier = Modifier.weight(1.2f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = downPaymentPctStr, onValueChange = { downPaymentPctStr = it }, label = { Text("Down (%)") }, modifier = Modifier.weight(0.8f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = interestRateStr, onValueChange = { interestRateStr = it }, label = { Text("Interest Rate (%)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = termYearsStr, onValueChange = { termYearsStr = it }, label = { Text("Loan Term (Years)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Mortgage Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            Text("Monthly Principal & Interest: $${fmt(monthlyPayment)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text("Down Payment: $${fmt(downPayment)}", fontFamily = FontFamily.Monospace)
            Text("Loan Principal Financed: $${fmt(principal)}", fontFamily = FontFamily.Monospace)
            Text("Total Interest over Term: $${fmt(totalInterest)}", fontFamily = FontFamily.Monospace)
            Text("Total Cost of Mortgage: $${fmt(totalPayment)}", fontFamily = FontFamily.Monospace)
        }
    }
}

private fun fmt(v: Double): String {
    return if (v == v.toLong().toDouble() && v < 1e9) {
        v.toLong().toString()
    } else {
        "%,.2f".format(v)
    }
}
