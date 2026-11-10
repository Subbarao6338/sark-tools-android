package omni.toolbox.utils

import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility for calculating the lexicographical rank of a word among all its possible permutations.
 */
object WordRankCalculator {

    private val factorialCache = ConcurrentHashMap<Int, BigInteger>()

    fun factorial(n: Int): BigInteger {
        if (n < 0) return BigInteger.ZERO
        if (n == 0 || n == 1) return BigInteger.ONE
        factorialCache[n]?.let { return it }
        var res = BigInteger.ONE
        for (i in 2..n) {
            res = res.multiply(BigInteger.valueOf(i.toLong()))
        }
        factorialCache[n] = res
        return res
    }

    /**
     * Calculates the 1-based lexicographical rank of the given word.
     */
    fun calculateRank(word: String): BigInteger {
        val upperWord = word.uppercase().filter { it.isLetterOrDigit() }
        if (upperWord.isEmpty()) return BigInteger.ZERO

        val len = upperWord.length
        var rank = BigInteger.ONE
        var mul = factorial(len)
        val charCount = mutableMapOf<Char, Int>()

        for (ch in upperWord) {
            charCount[ch] = charCount.getOrDefault(ch, 0) + 1
        }

        fun getFactorialDivisor(): BigInteger {
            var divisor = BigInteger.ONE
            for (count in charCount.values) {
                divisor = divisor.multiply(factorial(count))
            }
            return divisor
        }

        for (i in 0 until len) {
            mul = mul.divide(BigInteger.valueOf((len - i).toLong()))
            val divisor = getFactorialDivisor()
            var countSmaller = 0

            for ((char, count) in charCount) {
                if (char < upperWord[i]) {
                    countSmaller += count
                }
            }

            val term = BigInteger.valueOf(countSmaller.toLong()).multiply(mul).divide(divisor)
            rank = rank.add(term)

            charCount[upperWord[i]] = charCount[upperWord[i]]!! - 1
            if (charCount[upperWord[i]] == 0) {
                charCount.remove(upperWord[i])
            }
        }

        return rank
    }
}
