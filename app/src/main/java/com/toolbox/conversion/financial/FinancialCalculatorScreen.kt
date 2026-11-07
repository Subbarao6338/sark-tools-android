package com.toolbox.conversion.financial

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toolbox.core.ui.copyToClipboard
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

private val moneyFmt: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

// ----------------- Calculation Engines -----------------

fun calculateSimpleInterest(principal: Double, annualRate: Double, years: Double): Pair<Double, Double> {
    val interest = (principal * annualRate * years) / 100.0
    val total = principal + interest
    return Pair(interest, total)
}

fun calculateCompoundInterest(
    principal: Double,
    annualRate: Double,
    years: Double,
    timesPerYear: Int = 12,
): Pair<Double, Double> {
    val r = annualRate / 100.0
    val n = timesPerYear.coerceAtLeast(1)
    val amount = principal * (1.0 + r / n).pow(n * years)
    val interest = amount - principal
    return Pair(interest, amount)
}

data class ProfitLossResult(
    val isProfit: Boolean,
    val amount: Double,
    val percentage: Double,
)

fun calculateProfitLoss(costPrice: Double, sellingPrice: Double): ProfitLossResult {
    val diff = sellingPrice - costPrice
    val isProfit = diff >= 0
    val absAmount = kotlin.math.abs(diff)
    val percentage = if (costPrice > 0) (absAmount / costPrice) * 100.0 else 0.0
    return ProfitLossResult(isProfit, absAmount, percentage)
}

data class InvestmentResult(
    val investedAmount: Double,
    val totalReturns: Double,
    val finalValue: Double,
)

fun calculateInvestment(
    initialAmount: Double,
    monthlyAmount: Double,
    annualReturn: Double,
    years: Double,
): InvestmentResult {
    val r = annualReturn / 100.0 / 12.0
    val months = (years * 12).toInt()
    var futureValue = initialAmount * (1.0 + r).pow(months)

    if (r > 0) {
        futureValue += monthlyAmount * ((1.0 + r).pow(months) - 1.0) / r
    } else {
        futureValue += monthlyAmount * months
    }

    val totalInvested = initialAmount + (monthlyAmount * months)
    val returns = futureValue - totalInvested
    return InvestmentResult(totalInvested, returns, futureValue)
}

fun calculateSip(
    monthlyInvestment: Double,
    expectedReturnRate: Double,
    years: Double,
): InvestmentResult {
    val r = expectedReturnRate / 100.0 / 12.0
    val months = (years * 12).toInt()
    val futureValue = if (r > 0) {
        monthlyInvestment * (((1.0 + r).pow(months) - 1.0) / r) * (1.0 + r)
    } else {
        monthlyInvestment * months
    }
    val invested = monthlyInvestment * months
    val returns = futureValue - invested
    return InvestmentResult(invested, returns, futureValue)
}

data class SalaryResult(
    val grossSalary: Double,
    val totalDeductions: Double,
    val taxableIncome: Double,
    val taxAmount: Double,
    val netSalary: Double,
)

fun calculateSalary(
    basicSalary: Double,
    allowances: Double,
    deductions: Double,
    taxPercent: Double,
): SalaryResult {
    val gross = basicSalary + allowances
    val taxable = (gross - deductions).coerceAtLeast(0.0)
    val tax = (taxable * (taxPercent / 100.0)).coerceAtLeast(0.0)
    val totalDed = deductions + tax
    val net = (gross - totalDed).coerceAtLeast(0.0)
    return SalaryResult(gross, deductions, taxable, tax, net)
}

// ----------------- UI Screen -----------------

@Composable
fun FinancialCalculatorScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Simple Int", "Compound Int", "SIP", "Investment", "Profit & Loss", "Salary")

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, maxLines = 1) },
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
                0 -> SimpleInterestTab()
                1 -> CompoundInterestTab()
                2 -> SipTab()
                3 -> InvestmentTab()
                4 -> ProfitLossTab()
                5 -> SalaryTab()
            }
        }
    }
}

@Composable
private fun SimpleInterestTab() {
    val context = LocalContext.current
    var pInput by rememberSaveable { mutableStateOf("") }
    var rInput by rememberSaveable { mutableStateOf("") }
    var tInput by rememberSaveable { mutableStateOf("") }

    InputCard("Simple Interest (SI = P × R × T / 100)") {
        MoneyField(value = pInput, onValueChange = { pInput = it }, label = "Principal Amount (P)")
        RateField(value = rInput, onValueChange = { rInput = it }, label = "Annual Interest Rate (R %)")
        DurationField(value = tInput, onValueChange = { tInput = it }, label = "Time Period (Years)")
    }

    val p = pInput.toDoubleOrNull() ?: 0.0
    val r = rInput.toDoubleOrNull() ?: 0.0
    val t = tInput.toDoubleOrNull() ?: 0.0

    if (p > 0.0 && r > 0.0 && t > 0.0) {
        val (interest, total) = calculateSimpleInterest(p, r, t)
        Spacer(modifier = Modifier.height(16.dp))
        ResultCard(
            title = "Total Amount",
            mainValue = moneyFmt.format(total),
            subtitle = "Interest Earned: ${moneyFmt.format(interest)}",
            onCopy = { copyToClipboard(context, "Simple Interest", "Total: $total, Interest: $interest") },
        ) {
            FinanceRow("Principal Amount", moneyFmt.format(p))
            FinanceRow("Interest Earned", moneyFmt.format(interest))
            FinanceRow("Total Payable/Maturity", moneyFmt.format(total), isBold = true)
        }
    }
}

@Composable
private fun CompoundInterestTab() {
    val context = LocalContext.current
    var pInput by rememberSaveable { mutableStateOf("") }
    var rInput by rememberSaveable { mutableStateOf("") }
    var tInput by rememberSaveable { mutableStateOf("") }
    var freqIdx by rememberSaveable { mutableIntStateOf(3) } // 0: Annually, 1: Semi, 2: Quarterly, 3: Monthly

    val frequencies = listOf("Annual (1/yr)", "Semi-Annual (2/yr)", "Quarterly (4/yr)", "Monthly (12/yr)")
    val freqValues = listOf(1, 2, 4, 12)

    InputCard("Compound Interest: A = P(1 + r/n)^(nt)") {
        MoneyField(value = pInput, onValueChange = { pInput = it }, label = "Principal Amount (P)")
        RateField(value = rInput, onValueChange = { rInput = it }, label = "Annual Interest Rate (R %)")
        DurationField(value = tInput, onValueChange = { tInput = it }, label = "Time Period (Years)")

        Spacer(modifier = Modifier.height(12.dp))
        Text("Compounding Frequency", style = MaterialTheme.typography.labelSmall)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            frequencies.forEachIndexed { i, f ->
                FilterChip(
                    selected = freqIdx == i,
                    onClick = { freqIdx = i },
                    label = { Text(f, fontSize = 12.sp) },
                )
            }
        }
    }

    val p = pInput.toDoubleOrNull() ?: 0.0
    val r = rInput.toDoubleOrNull() ?: 0.0
    val t = tInput.toDoubleOrNull() ?: 0.0

    if (p > 0.0 && r > 0.0 && t > 0.0) {
        val (interest, total) = calculateCompoundInterest(p, r, t, freqValues[freqIdx])
        Spacer(modifier = Modifier.height(16.dp))
        ResultCard(
            title = "Maturity Value",
            mainValue = moneyFmt.format(total),
            subtitle = "Compound Interest: ${moneyFmt.format(interest)}",
            onCopy = { copyToClipboard(context, "Compound Interest", "Total: $total, CI: $interest") },
        ) {
            FinanceRow("Principal Amount", moneyFmt.format(p))
            FinanceRow("Total Interest", moneyFmt.format(interest))
            FinanceRow("Final Amount", moneyFmt.format(total), isBold = true)
        }
    }
}

@Composable
private fun SipTab() {
    val context = LocalContext.current
    var monthlyInput by rememberSaveable { mutableStateOf("") }
    var rateInput by rememberSaveable { mutableStateOf("") }
    var yearsInput by rememberSaveable { mutableStateOf("") }

    InputCard("Systematic Investment Plan (SIP)") {
        MoneyField(value = monthlyInput, onValueChange = { monthlyInput = it }, label = "Monthly Investment Amount")
        RateField(value = rateInput, onValueChange = { rateInput = it }, label = "Expected Annual Return (CAGR %)")
        DurationField(value = yearsInput, onValueChange = { yearsInput = it }, label = "Tenure / Duration (Years)")
    }

    val m = monthlyInput.toDoubleOrNull() ?: 0.0
    val r = rateInput.toDoubleOrNull() ?: 0.0
    val y = yearsInput.toDoubleOrNull() ?: 0.0

    if (m > 0.0 && r > 0.0 && y > 0.0) {
        val res = calculateSip(m, r, y)
        Spacer(modifier = Modifier.height(16.dp))
        ResultCard(
            title = "Expected Maturity Value",
            mainValue = moneyFmt.format(res.finalValue),
            subtitle = "Estimated Returns: ${moneyFmt.format(res.totalReturns)}",
            onCopy = { copyToClipboard(context, "SIP Returns", moneyFmt.format(res.finalValue)) },
        ) {
            FinanceRow("Total Invested Amount", moneyFmt.format(res.investedAmount))
            FinanceRow("Estimated Wealth Gain", moneyFmt.format(res.totalReturns))
            FinanceRow("Total Future Value", moneyFmt.format(res.finalValue), isBold = true)
        }
    }
}

@Composable
private fun InvestmentTab() {
    val context = LocalContext.current
    var initialInput by rememberSaveable { mutableStateOf("") }
    var monthlyInput by rememberSaveable { mutableStateOf("") }
    var rateInput by rememberSaveable { mutableStateOf("") }
    var yearsInput by rememberSaveable { mutableStateOf("") }

    InputCard("Comprehensive Investment Growth") {
        MoneyField(value = initialInput, onValueChange = { initialInput = it }, label = "Initial Lump Sum (Optional)")
        MoneyField(value = monthlyInput, onValueChange = { monthlyInput = it }, label = "Monthly Contribution (Optional)")
        RateField(value = rateInput, onValueChange = { rateInput = it }, label = "Expected Annual Return (%)")
        DurationField(value = yearsInput, onValueChange = { yearsInput = it }, label = "Investment Period (Years)")
    }

    val init = initialInput.toDoubleOrNull() ?: 0.0
    val month = monthlyInput.toDoubleOrNull() ?: 0.0
    val r = rateInput.toDoubleOrNull() ?: 0.0
    val y = yearsInput.toDoubleOrNull() ?: 0.0

    if ((init > 0.0 || month > 0.0) && r > 0.0 && y > 0.0) {
        val res = calculateInvestment(init, month, r, y)
        Spacer(modifier = Modifier.height(16.dp))
        ResultCard(
            title = "Projected Portfolio Value",
            mainValue = moneyFmt.format(res.finalValue),
            subtitle = "Capital Gain: ${moneyFmt.format(res.totalReturns)}",
            onCopy = { copyToClipboard(context, "Investment Projection", moneyFmt.format(res.finalValue)) },
        ) {
            FinanceRow("Total Capital Invested", moneyFmt.format(res.investedAmount))
            FinanceRow("Accumulated Interest/Growth", moneyFmt.format(res.totalReturns))
            FinanceRow("Total Projected Value", moneyFmt.format(res.finalValue), isBold = true)
        }
    }
}

@Composable
private fun ProfitLossTab() {
    val context = LocalContext.current
    var costInput by rememberSaveable { mutableStateOf("") }
    var sellInput by rememberSaveable { mutableStateOf("") }

    InputCard("Profit and Loss Calculator") {
        MoneyField(value = costInput, onValueChange = { costInput = it }, label = "Cost Price (CP)")
        MoneyField(value = sellInput, onValueChange = { sellInput = it }, label = "Selling Price (SP)")
    }

    val cp = costInput.toDoubleOrNull() ?: 0.0
    val sp = sellInput.toDoubleOrNull() ?: 0.0

    if (cp > 0.0 && sp > 0.0) {
        val res = calculateProfitLoss(cp, sp)
        Spacer(modifier = Modifier.height(16.dp))
        val statusText = if (res.isProfit) "Profit" else "Loss"
        val statusColor = if (res.isProfit) Color(0xFF2E7D32) else Color(0xFFC62828)

        ResultCard(
            title = "$statusText Amount",
            mainValue = moneyFmt.format(res.amount),
            subtitle = "$statusText Percentage: ${moneyFmt.format(res.percentage)}%",
            onCopy = { copyToClipboard(context, "Profit & Loss", "$statusText: ${res.amount}") },
        ) {
            FinanceRow("Cost Price", moneyFmt.format(cp))
            FinanceRow("Selling Price", moneyFmt.format(sp))
            FinanceRow(statusText, "${if (res.isProfit) "+" else "-"}${moneyFmt.format(res.amount)} (${moneyFmt.format(res.percentage)}%)", isBold = true)
        }
    }
}

@Composable
private fun SalaryTab() {
    val context = LocalContext.current
    var basicInput by rememberSaveable { mutableStateOf("") }
    var allowancesInput by rememberSaveable { mutableStateOf("") }
    var deductionsInput by rememberSaveable { mutableStateOf("") }
    var taxRateInput by rememberSaveable { mutableStateOf("") }

    InputCard("Salary & Take-Home Calculator") {
        MoneyField(value = basicInput, onValueChange = { basicInput = it }, label = "Basic Salary (per month/year)")
        MoneyField(value = allowancesInput, onValueChange = { allowancesInput = it }, label = "HRA & Other Allowances")
        MoneyField(value = deductionsInput, onValueChange = { deductionsInput = it }, label = "PF & Insurance Deductions")
        RateField(value = taxRateInput, onValueChange = { taxRateInput = it }, label = "Applicable Income Tax Rate (%)")
    }

    val b = basicInput.toDoubleOrNull() ?: 0.0
    val a = allowancesInput.toDoubleOrNull() ?: 0.0
    val d = deductionsInput.toDoubleOrNull() ?: 0.0
    val t = taxRateInput.toDoubleOrNull() ?: 0.0

    if (b > 0.0) {
        val res = calculateSalary(b, a, d, t)
        Spacer(modifier = Modifier.height(16.dp))
        ResultCard(
            title = "Net Take-Home Salary",
            mainValue = moneyFmt.format(res.netSalary),
            subtitle = "Gross Earnings: ${moneyFmt.format(res.grossSalary)}",
            onCopy = { copyToClipboard(context, "Net Salary", moneyFmt.format(res.netSalary)) },
        ) {
            FinanceRow("Gross Salary", moneyFmt.format(res.grossSalary))
            FinanceRow("Standard Deductions", "- ${moneyFmt.format(res.totalDeductions)}")
            FinanceRow("Taxable Income", moneyFmt.format(res.taxableIncome))
            FinanceRow("Income Tax (${t}%)", "- ${moneyFmt.format(res.taxAmount)}")
            FinanceRow("Net Take-Home Salary", moneyFmt.format(res.netSalary), isBold = true)
        }
    }
}

// ----------------- Helpers -----------------

@Composable
private fun InputCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun MoneyField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        singleLine = true,
    )
}

@Composable
private fun RateField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        singleLine = true,
    )
}

@Composable
private fun DurationField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        singleLine = true,
    )
}

@Composable
private fun ResultCard(
    title: String,
    mainValue: String,
    subtitle: String,
    onCopy: () -> Unit,
    breakdown: @Composable () -> Unit,
) {
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
                Text(title, style = MaterialTheme.typography.labelMedium)
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                }
            }
            Text(
                text = mainValue,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            breakdown()
        }
    }
}

@Composable
private fun FinanceRow(label: String, value: String, isBold: Boolean = false) {
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
