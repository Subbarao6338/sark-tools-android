package com.toolbox.conversion

import com.toolbox.conversion.bmr.ActivityLevel
import com.toolbox.conversion.bmr.BmrCalculatorEngine
import com.toolbox.conversion.bmr.Gender
import com.toolbox.conversion.contrast.ContrastCalculatorEngine
import com.toolbox.conversion.recipe.RecipeScalerEngine
import com.toolbox.conversion.units.DataStorageConverterEngine
import com.toolbox.conversion.units.LengthConverterEngine
import com.toolbox.conversion.units.TemperatureConverterEngine
import com.toolbox.conversion.units.WeightConverterEngine
import com.toolbox.conversion.units.dataStorageUnits
import com.toolbox.conversion.units.lengthUnits
import com.toolbox.conversion.units.weightUnits
import com.toolbox.conversion.units.TempScale
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitConverterSuiteTest {

    @Test
    fun testBmrCalculation() {
        // Mifflin-St Jeor:
        // Male: 10*70 + 6.25*175 - 5*25 + 5 = 700 + 1093.75 - 125 + 5 = 1673.75
        val bmr = BmrCalculatorEngine.calculateBmr(Gender.Male, 70.0, 175.0, 25)
        assertEquals(1673.75, bmr, 0.1)

        val tdee = BmrCalculatorEngine.calculateTdee(bmr, ActivityLevel.Moderate)
        assertEquals(1673.75 * 1.55, tdee, 0.1)
    }

    @Test
    fun testWcagContrastRatio() {
        // Black on White should be 21:1
        val black = Color(0f, 0f, 0f)
        val white = Color(1f, 1f, 1f)
        val ratio = ContrastCalculatorEngine.contrastRatio(black, white)
        assertEquals(21.0, ratio, 0.2)

        // Same color should be 1:1
        val sameRatio = ContrastCalculatorEngine.contrastRatio(white, white)
        assertEquals(1.0, sameRatio, 0.05)
    }

    @Test
    fun testRecipeScaling() {
        val factor = RecipeScalerEngine.calculateScaleFactor(4.0, 8.0)
        assertEquals(2.0, factor, 0.01)

        val scaled = RecipeScalerEngine.scaleAmount(250.0, factor)
        assertEquals(500.0, scaled, 0.01)
    }

    @Test
    fun testLengthConversion() {
        val meter = lengthUnits.first { it.id == "m" }
        val kilometer = lengthUnits.first { it.id == "km" }
        val inch = lengthUnits.first { it.id == "in" }

        assertEquals(1.0, LengthConverterEngine.convert(1000.0, meter, kilometer), 0.001)
        assertEquals(39.3701, LengthConverterEngine.convert(1.0, meter, inch), 0.01)
    }

    @Test
    fun testWeightConversion() {
        val kg = weightUnits.first { it.id == "kg" }
        val lb = weightUnits.first { it.id == "lb" }

        assertEquals(2.20462, WeightConverterEngine.convert(1.0, kg, lb), 0.01)
    }

    @Test
    fun testTemperatureConversion() {
        assertEquals(32.0, TemperatureConverterEngine.convert(0.0, TempScale.Celsius, TempScale.Fahrenheit), 0.01)
        assertEquals(212.0, TemperatureConverterEngine.convert(100.0, TempScale.Celsius, TempScale.Fahrenheit), 0.01)
        assertEquals(273.15, TemperatureConverterEngine.convert(0.0, TempScale.Celsius, TempScale.Kelvin), 0.01)
    }

    @Test
    fun testDataStorageConversion() {
        val mb = dataStorageUnits.first { it.id == "mb" }
        val gb = dataStorageUnits.first { it.id == "gb" }

        assertEquals(1.0, DataStorageConverterEngine.convert(1000.0, mb, gb), 0.001)
    }
}
