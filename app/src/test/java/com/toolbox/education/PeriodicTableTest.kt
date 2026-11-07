package com.toolbox.education

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PeriodicTableTest {

    @Test
    fun testAll118ElementsPresent() {
        val elements = PeriodicTableData.elements
        assertEquals(118, elements.size)

        // Verify sequential atomic numbers 1 to 118
        for (i in 1..118) {
            val el = PeriodicTableData.getByNumber(i)
            assertNotNull("Element with atomic number $i must exist", el)
            assertEquals(i, el?.number)
        }
    }

    @Test
    fun testKeyElementsDataIntegrity() {
        val h = PeriodicTableData.getBySymbol("H")
        assertNotNull(h)
        assertEquals("Hydrogen", h?.name)
        assertEquals(1, h?.number)
        assertEquals(ElementState.Gas, h?.standardState)

        val au = PeriodicTableData.getBySymbol("Au")
        assertNotNull(au)
        assertEquals("Gold", au?.name)
        assertEquals(79, au?.number)
        assertEquals(ElementCategory.TransitionMetal, au?.category)

        val og = PeriodicTableData.getByNumber(118)
        assertNotNull(og)
        assertEquals("Og", og?.symbol)
        assertEquals("Oganesson", og?.name)
    }

    @Test
    fun testSearchFunctionality() {
        val resultsByName = PeriodicTableData.search("Iron")
        assertTrue(resultsByName.any { it.symbol == "Fe" })

        val resultsBySymbol = PeriodicTableData.search("Fe")
        assertTrue(resultsBySymbol.any { it.name == "Iron" })

        val resultsByNumber = PeriodicTableData.search("26")
        assertTrue(resultsByNumber.any { it.name == "Iron" })
    }
}
