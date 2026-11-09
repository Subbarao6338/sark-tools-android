package com.toolbox.conversion

import com.toolbox.conversion.basiccalc.evaluateBasicExpression
import com.toolbox.conversion.fraction.computeFractionResult
import com.toolbox.conversion.fraction.decimalToFraction
import com.toolbox.conversion.fraction.simplifyFractionResult
import com.toolbox.conversion.ratio.divideRatio
import com.toolbox.conversion.ratio.simplifyRatio
import com.toolbox.conversion.ratio.solveProportion
import com.toolbox.conversion.unitconverter.UnitCategory
import com.toolbox.conversion.unitconverter.allUnits
import com.toolbox.everyday.textsuite.convertNumberToWords
import com.toolbox.everyday.textsuite.fromRoman
import com.toolbox.everyday.textsuite.toRoman
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MultiToolSpecSuiteTest {

    @Test
    fun testRegionalAreaUnits() {
        val areaUnits = allUnits.filter { it.category == UnitCategory.Area }
        val bigha = areaUnits.find { it.name == "Bigha" }
        val gunta = areaUnits.find { it.name == "Gunta" }
        val cent = areaUnits.find { it.name == "Cent" }

        assertTrue("Bigha must be present in Area units", bigha != null)
        assertTrue("Gunta must be present in Area units", gunta != null)
        assertTrue("Cent must be present in Area units", cent != null)

        // Test conversions to base (m²)
        assertEquals(2529.285, bigha!!.toBase(1.0), 0.01)
        assertEquals(101.1714, gunta!!.toBase(1.0), 0.01)
        assertEquals(40.4686, cent!!.toBase(1.0), 0.01)
    }

    @Test
    fun testBasicCalculatorEvaluation() {
        assertEquals(14.0, evaluateBasicExpression("2+3*4"), 0.0001)
        assertEquals(10.0, evaluateBasicExpression("20/2"), 0.0001)
        assertEquals(-5.0, evaluateBasicExpression("10-15"), 0.0001)
        assertEquals(7.5, evaluateBasicExpression("5+5/2"), 0.0001)
    }

    @Test
    fun testFractionCalculator() {
        val addRes = computeFractionResult("", "1", "2", "+", "", "1", "4")
        assertEquals("3 / 4", addRes.first)

        val mulRes = computeFractionResult("", "2", "3", "×", "", "3", "4")
        assertEquals("1 / 2", mulRes.first)

        val mixedRes = computeFractionResult("1", "1", "2", "+", "2", "1", "2")
        assertEquals("4", mixedRes.first)

        val simplified = simplifyFractionResult("", "12", "16")
        assertEquals("3 / 4", simplified.first)

        val decToFrac = decimalToFraction("0.75")
        assertTrue(decToFrac.startsWith("3 / 4"))
    }

    @Test
    fun testRatioCalculator() {
        // A : B = C : D -> 2 : 5 = 10 : ? -> D should be 25
        val prop = solveProportion("2", "5", "10", "")
        assertEquals("D = 25.0", prop.first)

        // Simplify 1920 : 1080 -> 16 : 9
        val sim = simplifyRatio("1920", "1080")
        assertEquals("16 : 9", sim.first)

        // Divide 350 in 3 : 4 -> A = 150.00, B = 200.00
        val div = divideRatio("3", "4", "350")
        assertEquals("A = 150.00  |  B = 200.00", div.first)
    }

    @Test
    fun testNumberToWords() {
        assertEquals("One Hundred", convertNumberToWords("100", false))
        assertEquals("One Thousand Two Hundred Thirty Four", convertNumberToWords("1234", false))
        assertEquals("One Lakh", convertNumberToWords("100000", true))
        assertEquals("One Crore", convertNumberToWords("10000000", true))
    }

    @Test
    fun testRomanNumerals() {
        assertEquals("MMXXIV", toRoman(2024))
        assertEquals("XIV", toRoman(14))
        assertEquals("IX", toRoman(9))
        assertEquals(2024, fromRoman("MMXXIV"))
        assertEquals(14, fromRoman("XIV"))
        assertEquals(9, fromRoman("IX"))
    }
}
