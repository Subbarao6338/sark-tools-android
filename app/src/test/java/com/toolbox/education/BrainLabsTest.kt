package com.toolbox.education

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BrainLabsTest {

    @Test
    fun testTranslateRnaToAminoAcids() {
        val rna = "AUGUUUUGA" // Met, Phe, Stop
        val result = ScienceEngine.translateRnaToAminoAcids(rna)
        assertEquals(3, result.size)
        assertEquals("AUG" to "Met (Start)", result[0])
        assertEquals("UUU" to "Phe (Phenylalanine)", result[1])
        assertEquals("UGA" to "STOP (Opal)", result[2])
    }

    @Test
    fun testPunnettSquareMendelianRatio() {
        val offspring = ScienceEngine.computePunnettSquare("Aa", "Aa")
        assertEquals(4, offspring.size)
        val counts = offspring.groupingBy { it }.eachCount()
        assertEquals(1, counts["AA"] ?: 0)
        assertEquals(2, counts["Aa"] ?: 0)
        assertEquals(1, counts["aa"] ?: 0)
    }

    @Test
    fun testPhysicsPendulumAndProjectile() {
        val period = ScienceEngine.computePendulumPeriod(1.0, 9.80665)
        assertTrue(period in 2.00..2.01)

        val (flightTime, range, height) = ScienceEngine.computeProjectileMotion(20.0, 45.0, 9.80665)
        assertTrue(flightTime > 0)
        assertTrue(range > 0)
        assertTrue(height > 0)
    }

    @Test
    fun testBrainEngineMathAndSequences() {
        val mathQ = BrainEngine.generateMathQuestion()
        assertTrue(mathQ.options.contains(mathQ.answer))
        assertEquals(4, mathQ.options.size)

        val seqProblem = BrainEngine.generateSequenceProblem()
        assertTrue(seqProblem.options.contains(seqProblem.answer))
        assertEquals(5, seqProblem.sequence.size)
    }
}
