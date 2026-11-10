package com.toolbox.datalab

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataLabTest {

    @Test
    fun testStatisticalCalculationsAndAnomalies() {
        val numbers = listOf(10.0, 12.0, 14.0, 16.0, 18.0, 20.0, 100.0)
        val stats = DataLabLogic.calculateStats(numbers)

        assertEquals(7.0, stats["Count"])
        assertNotNull(stats["Mean"])
        assertNotNull(stats["Median"])
        assertNotNull(stats["StdDev"])
        assertNotNull(stats["IQR"])

        val anomalies = DataLabLogic.detectAnomalies(numbers, zThreshold = 1.8)
        assertTrue("100.0 should be detected as anomaly", anomalies.contains(100.0))
    }

    @Test
    fun testParseDocumentCsvAndTsv() {
        val csv = "Name,Score\nAlice,95\nBob,88"
        val parsedCsv = DataLabLogic.parseDocument(csv)
        assertEquals("CSV", parsedCsv.format)
        assertEquals(listOf("Name", "Score"), parsedCsv.headers)
        assertEquals(2, parsedCsv.rows.size)

        val tsv = "ColA\tColB\nVal1\tVal2"
        val parsedTsv = DataLabLogic.parseDocument(tsv)
        assertEquals("TSV", parsedTsv.format)
        assertEquals(listOf("ColA", "ColB"), parsedTsv.headers)
        assertEquals(1, parsedTsv.rows.size)
    }

    @Test
    fun testParseDocumentJsonAndKeyValue() {
        val json = "[{\"id\": 1, \"name\": \"Alex\"}, {\"id\": 2, \"name\": \"Sam\"}]"
        val parsedJson = DataLabLogic.parseDocument(json)
        assertEquals("JSON", parsedJson.format)
        assertEquals(2, parsedJson.rows.size)

        val kv = "host=localhost\nport=8080\nenv=production"
        val parsedKv = DataLabLogic.parseDocument(kv)
        assertEquals("KEY_VALUE", parsedKv.format)
        assertEquals(3, parsedKv.rows.size)
    }

    @Test
    fun testReconciliationAndMockGeneration() {
        val listA = listOf("A", "B", "C")
        val listB = listOf("B", "C", "D")
        val rec = DataLabLogic.reconcileLists(listA, listB)

        assertEquals(listOf("B", "C"), rec["Matched"]?.sorted())
        assertEquals(listOf("A"), rec["Only in A"])
        assertEquals(listOf("D"), rec["Only in B"])

        val mockUsers = DataLabLogic.generateMockUsers(5)
        assertEquals(5, mockUsers.size)
        assertTrue(mockUsers[0].contains("email"))

        val mockOrders = DataLabLogic.generateMockOrders(3)
        assertEquals(3, mockOrders.size)
        assertTrue(mockOrders[0].contains("orderId"))
    }
}
