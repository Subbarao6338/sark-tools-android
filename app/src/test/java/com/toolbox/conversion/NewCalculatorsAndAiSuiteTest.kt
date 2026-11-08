package com.toolbox.conversion

import com.toolbox.ai.PerchanceGeneratorEngine
import com.toolbox.conversion.algebra.solveCubic
import com.toolbox.conversion.calculator.CalculatorViewModel
import com.toolbox.dashboard.ToolCategory
import com.toolbox.dashboard.allTools
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

class NewCalculatorsAndAiSuiteTest {

    @Test
    fun testAllNewToolsRegisteredInAllTools() {
        val toolIds = allTools.map { it.id }.toSet()
        assertTrue("Equation solver must be registered", toolIds.contains("equation_solver"))
        assertTrue("Geometry calculator must be registered", toolIds.contains("geometry_calculator"))
        assertTrue("Investment calculator must be registered", toolIds.contains("investment_calculator"))
        assertTrue("GPA calculator must be registered", toolIds.contains("gpa_calculator"))
        assertTrue("Perchance AI must be registered", toolIds.contains("perchance_ai"))

        val eqTool = allTools.first { it.id == "equation_solver" }
        assertEquals(ToolCategory.Calculators, eqTool.category)

        val aiTool = allTools.first { it.id == "perchance_ai" }
        assertEquals(ToolCategory.Utilities, aiTool.category)
    }

    @Test
    fun testCubicEquationCardanoSolver() {
        // (x - 1)(x - 2)(x - 3) = x^3 - 6x^2 + 11x - 6 = 0
        // Roots should be 1, 2, 3
        val roots = solveCubic(1.0, -6.0, 11.0, -6.0)
        assertEquals(3, roots.size)
        val doubleRoots = roots.map { it.toDouble() }.sorted()
        assertEquals(1.0, doubleRoots[0], 1e-4)
        assertEquals(2.0, doubleRoots[1], 1e-4)
        assertEquals(3.0, doubleRoots[2], 1e-4)
    }

    @Test
    fun testPythagoreanGeometryCalculations() {
        // Standard 3-4-5 right triangle
        val a = 3.0
        val b = 4.0
        val c = hypot(a, b)
        assertEquals(5.0, c, 1e-6)

        val area = 0.5 * a * b
        assertEquals(6.0, area, 1e-6)

        val perimeter = a + b + c
        assertEquals(12.0, perimeter, 1e-6)
    }

    @Test
    fun testCalculatorMemoryAndFractionSupport() {
        val vm = CalculatorViewModel()

        // 1/3 fraction check
        vm.onInput("1/3")
        vm.onEquals()
        val state = vm.state.value
        assertEquals("1/3", state.fractionResult)

        // Memory Add M+
        vm.memoryAdd()
        assertTrue(vm.state.value.hasMemory)
        assertEquals(1.0 / 3.0, vm.state.value.memoryValue, 1e-4)

        // Memory Clear MC
        vm.memoryClear()
        assertFalse(vm.state.value.hasMemory)
        assertEquals(0.0, vm.state.value.memoryValue, 1e-6)
    }

    @Test
    fun testPerchanceAiProceduralGenerations() {
        val backstory = PerchanceGeneratorEngine.generateCreativeBackstory()
        assertNotNull(backstory)
        assertTrue(backstory.contains("Protagonist:"))
        assertTrue(backstory.contains("Core Objective:"))

        val photoPrompt = PerchanceGeneratorEngine.generateAiPhotoPrompt()
        assertNotNull(photoPrompt)
        assertTrue(photoPrompt.contains("Prompt:"))
        assertTrue(photoPrompt.contains("Negative Prompt:"))

        val hook = PerchanceGeneratorEngine.generateStoryHook()
        assertNotNull(hook)
        assertTrue(hook.isNotBlank())

        val codePrompt = PerchanceGeneratorEngine.generateCodingChallenge()
        assertNotNull(codePrompt)
        assertTrue(codePrompt.isNotBlank())
    }
}
