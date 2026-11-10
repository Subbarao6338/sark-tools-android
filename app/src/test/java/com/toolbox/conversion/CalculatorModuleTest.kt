package com.toolbox.conversion

import com.toolbox.conversion.calculator.ExpressionParser
import com.toolbox.conversion.discount.calculateDiscount
import com.toolbox.conversion.discount.compareDeals
import com.toolbox.conversion.financial.calculateCompoundInterest
import com.toolbox.conversion.financial.calculateInvestment
import com.toolbox.conversion.financial.calculateProfitLoss
import com.toolbox.conversion.financial.calculateSalary
import com.toolbox.conversion.financial.calculateSimpleInterest
import com.toolbox.conversion.financial.calculateSip
import com.toolbox.conversion.gst.calculateGst
import com.toolbox.conversion.mathutils.calculateBigFactorial
import com.toolbox.conversion.mathutils.calculateMultiGcdLcm
import com.toolbox.conversion.mathutils.calculateStatistics
import com.toolbox.conversion.mathutils.checkPrime
import com.toolbox.conversion.percentage.calculatePercentResult
import com.toolbox.conversion.unitconverter.UnitCategory
import com.toolbox.conversion.unitconverter.allUnits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class CalculatorModuleTest {

    // --- Expression Parser / Scientific Calculator ---

    @Test
    fun testBasicArithmetic() {
        val parser = ExpressionParser("10 + 20 * 5", useDegrees = true)
        assertEquals(110.0, parser.parse(), 1e-6)
    }

    @Test
    fun testScientificFunctions() {
        val sin30 = ExpressionParser("sin(30)", useDegrees = true).parse()
        assertEquals(0.5, sin30, 1e-6)

        val cos60 = ExpressionParser("cos(60)", useDegrees = true).parse()
        assertEquals(0.5, cos60, 1e-6)

        val sqrt144 = ExpressionParser("sqrt(144)", useDegrees = true).parse()
        assertEquals(12.0, sqrt144, 1e-6)

        val cbrt27 = ExpressionParser("cbrt(27)", useDegrees = true).parse()
        assertEquals(3.0, cbrt27, 1e-6)

        val log100 = ExpressionParser("log(100)", useDegrees = true).parse()
        assertEquals(2.0, log100, 1e-6)

        val factorial5 = ExpressionParser("5!", useDegrees = true).parse()
        assertEquals(120.0, factorial5, 1e-6)

        val power = ExpressionParser("2^10", useDegrees = true).parse()
        assertEquals(1024.0, power, 1e-6)
    }

    @Test
    fun testPermutationsAndCombinations() {
        val p = ExpressionParser("5P2", useDegrees = true).parse()
        assertEquals(20.0, p, 1e-6)

        val c = ExpressionParser("5C2", useDegrees = true).parse()
        assertEquals(10.0, c, 1e-6)
    }

    // --- Percentage Calculator ---

    @Test
    fun testPercentageCalculations() {
        // Mode 0: X% of Y (20% of 500 = 100)
        val res0 = calculatePercentResult(0, "20", "500")
        assertNotNull(res0)
        assertEquals("100", res0?.value)

        // Mode 1: What % (30 is what % of 200 = 15%)
        val res1 = calculatePercentResult(1, "30", "200")
        assertEquals("15%", res1?.value)

        // Mode 2: % Change (100 to 125 = +25%)
        val res2 = calculatePercentResult(2, "100", "125")
        assertEquals("+25%", res2?.value)

        // Mode 3: % Difference
        val res3 = calculatePercentResult(3, "50", "70")
        assertNotNull(res3)

        // Mode 4: Reverse percentage (120 with 20% markup -> 100)
        val res4 = calculatePercentResult(4, "120", "20")
        assertEquals("100", res4?.value)
    }

    // --- Discount Calculator ---

    @Test
    fun testDiscountCalculations() {
        // Original price 1000, 20% discount -> 800 final price, 200 discount
        val d1 = calculateDiscount(1000.0, 20.0, 0.0, 0.0)
        assertEquals(800.0, d1.finalPrice, 1e-6)
        assertEquals(200.0, d1.discountAmount, 1e-6)

        // Successive discounts: 1000 - 20% = 800, then - 10% = 720
        val d2 = calculateDiscount(1000.0, 20.0, 10.0, 0.0)
        assertEquals(720.0, d2.finalPrice, 1e-6)
        assertEquals(280.0, d2.discountAmount, 1e-6)

        // Discount with Tax: 1000 - 20% = 800, + 10% tax = 880
        val d3 = calculateDiscount(1000.0, 20.0, 0.0, 10.0)
        assertEquals(880.0, d3.finalPrice, 1e-6)
        assertEquals(80.0, d3.taxAmount, 1e-6)

        // Deal comparison
        val cmp = compareDeals(1000.0, 20.0, 1200.0, 30.0)
        assertEquals(800.0, cmp.deal1Final, 1e-6)
        assertEquals(840.0, cmp.deal2Final, 1e-6)
        assertTrue(cmp.betterDeal.contains("Deal A"))
    }

    // --- GST Calculator ---

    @Test
    fun testGstCalculations() {
        // Exclusive: Base 1000, 18% GST -> GST 180, Total 1180
        val excl = calculateGst(1000.0, 18.0, isInclusive = false, isInterState = false)
        assertEquals(1000.0, excl.baseAmount, 1e-6)
        assertEquals(180.0, excl.gstAmount, 1e-6)
        assertEquals(1180.0, excl.finalAmount, 1e-6)
        assertEquals(90.0, excl.cgst, 1e-6)
        assertEquals(90.0, excl.sgst, 1e-6)

        // Inclusive: Total 1180, 18% GST -> Base 1000, GST 180
        val incl = calculateGst(1180.0, 18.0, isInclusive = true, isInterState = true)
        assertEquals(1000.0, incl.baseAmount, 1e-4)
        assertEquals(180.0, incl.gstAmount, 1e-4)
        assertEquals(1180.0, incl.finalAmount, 1e-4)
        assertEquals(180.0, incl.igst, 1e-4)
    }

    // --- Financial Calculators ---

    @Test
    fun testFinancialFormulas() {
        // Simple Interest: P=10000, R=5%, T=2 years -> SI=1000, Total=11000
        val (si, totalSi) = calculateSimpleInterest(10000.0, 5.0, 2.0)
        assertEquals(1000.0, si, 1e-6)
        assertEquals(11000.0, totalSi, 1e-6)

        // Compound Interest: P=10000, R=10%, T=1 year, compounding monthly (timesPerYear=12)
        val (ci, totalCi) = calculateCompoundInterest(10000.0, 10.0, 1.0, timesPerYear = 12)
        assertTrue(ci > 1000.0) // CI exceeds simple interest
        assertEquals(10000.0 + ci, totalCi, 1e-6)

        // Profit & Loss: Cost 500, Sell 650 -> Profit 150 (30%)
        val pl = calculateProfitLoss(500.0, 650.0)
        assertTrue(pl.isProfit)
        assertEquals(150.0, pl.amount, 1e-6)
        assertEquals(30.0, pl.percentage, 1e-6)

        // SIP: Monthly 5000, 12% annual return, 1 year
        val sip = calculateSip(5000.0, 12.0, 1.0)
        assertEquals(60000.0, sip.investedAmount, 1e-6)
        assertTrue(sip.finalValue > 60000.0)

        // Salary: Basic 50000, allowances 20000, deductions 10000, tax 10%
        val sal = calculateSalary(50000.0, 20000.0, 10000.0, 10.0)
        assertEquals(70000.0, sal.grossSalary, 1e-6)
        assertEquals(60000.0, sal.taxableIncome, 1e-6)
        assertEquals(6000.0, sal.taxAmount, 1e-6)
        assertEquals(54000.0, sal.netSalary, 1e-6)
    }

    // --- Mathematical Utilities ---

    @Test
    fun testMathUtilities() {
        // Prime Checker
        val p17 = checkPrime(17)
        assertTrue(p17.isPrime)
        assertEquals(19L, p17.nextPrime)
        assertEquals(13L, p17.prevPrime)

        val p20 = checkPrime(20)
        assertFalse(p20.isPrime)
        assertEquals(listOf(2L, 2L, 5L), p20.factors)

        // Factorial
        val fact5 = calculateBigFactorial(5)
        assertEquals("120", fact5.toString())

        // GCD & LCM
        val gcdLcm = calculateMultiGcdLcm(listOf(24L, 36L, 60L))
        assertNotNull(gcdLcm)
        assertEquals(12L, gcdLcm?.first)
        assertEquals(360L, gcdLcm?.second)

        // Statistics
        val stats = calculateStatistics(listOf(10.0, 20.0, 20.0, 40.0, 60.0))
        assertNotNull(stats)
        assertEquals(5, stats?.count)
        assertEquals(150.0, stats?.sum ?: 0.0, 1e-6)
        assertEquals(30.0, stats?.mean ?: 0.0, 1e-6)
        assertEquals(20.0, stats?.median ?: 0.0, 1e-6)
        assertEquals(listOf(20.0), stats?.mode)
        assertEquals(10.0, stats?.min ?: 0.0, 1e-6)
        assertEquals(60.0, stats?.max ?: 0.0, 1e-6)
    }

    // --- Unit Converter Verification ---

    @Test
    fun testUnitConverterCategories() {
        val dataUnits = allUnits.filter { it.category == UnitCategory.Data }
        assertTrue(dataUnits.any { it.symbol == "kB" })
        assertTrue(dataUnits.any { it.symbol == "KiB" })
        assertTrue(dataUnits.any { it.symbol == "GB" })
        assertTrue(dataUnits.any { it.symbol == "GiB" })

        val timeUnits = allUnits.filter { it.category == UnitCategory.Time }
        assertTrue(timeUnits.any { it.symbol == "mo" })
        assertTrue(timeUnits.any { it.symbol == "yr" })
    }

    @Test
    fun testAllUnitCategoriesPresentInUnitConverters() {
        val unitConverterTools = com.toolbox.dashboard.allTools.filter {
            it.category == com.toolbox.dashboard.ToolCategory.UnitConverters
        }
        val toolNames = unitConverterTools.map { it.name }

        // Ensure every UnitCategory in UnitCategory.entries is represented as a tool
        UnitCategory.entries.forEach { category ->
            val matchFound = when (category) {
                UnitCategory.Length -> toolNames.contains("Length")
                UnitCategory.Weight -> toolNames.contains("Weight & Mass")
                UnitCategory.Temperature -> toolNames.contains("Temperature")
                UnitCategory.Data -> toolNames.contains("Data Storage")
                UnitCategory.FuelEconomy -> toolNames.contains("Fuel Economy") || toolNames.contains("Fuel Economy Converter")
                else -> toolNames.contains(category.label)
            }
            assertTrue("Expected tool for category ${category.label} in UnitConverters", matchFound)
        }
    }
}
