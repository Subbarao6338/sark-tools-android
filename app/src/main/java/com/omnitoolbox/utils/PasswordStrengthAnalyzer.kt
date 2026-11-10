package omni.toolbox.utils

import kotlin.math.ln
import kotlin.math.log

object PasswordStrengthAnalyzer {

    // Common weak password dictionary
    private val WEAK_PASSWORDS = setOf(
        "123456", "password", "123456789", "12345", "12345678", "1234", "qwerty", "admin", "letmein", "welcome",
        "iloveyou", "sunshine", "princess", "dragon", "111111", "abc123"
    )

    // Common keyboard sequences / patterns
    private val KEYBOARD_PATTERNS = listOf(
        "qwertyuiop", "asdfghjkl", "zxcvbnm", "1234567890", "0987654321"
    )

    data class AnalysisResult(
        val entropy: Double,
        val length: Int,
        val hasUppercase: Boolean,
        val hasLowercase: Boolean,
        val hasDigit: Boolean,
        val hasSpecial: Boolean,
        val isCommonWeak: Boolean,
        val containsKeyboardPattern: Boolean,
        val strengthGrade: StrengthGrade,
        val feedback: List<String>
    )

    enum class StrengthGrade {
        VERY_WEAK, WEAK, MEDIUM, STRONG, VERY_STRONG
    }

    /**
     * Calculates Shannon entropy for a given password string:
     * H = -Sum(p_i * log2(p_i))
     */
    fun calculateShannonEntropy(password: String): Double {
        if (password.isEmpty()) return 0.0
        val len = password.length.toDouble()
        val frequencies = password.groupingBy { it }.eachCount()
        var entropy = 0.0
        for (count in frequencies.values) {
            val p = count.toDouble() / len
            entropy -= p * (ln(p) / ln(2.0))
        }
        return entropy
    }

    /**
     * Estimates cryptographic key space entropy:
     * Entropy = L * log2(R)
     * where L is length and R is the size of the character pool.
     */
    fun calculatePoolEntropy(password: String): Double {
        if (password.isEmpty()) return 0.0
        var poolSize = 0
        if (password.any { it.isLowerCase() }) poolSize += 26
        if (password.any { it.isUpperCase() }) poolSize += 26
        if (password.any { it.isDigit() }) poolSize += 10
        // Special characters pool estimate
        if (password.any { !it.isLetterOrDigit() }) poolSize += 33

        if (poolSize == 0) return 0.0
        return password.length * (ln(poolSize.toDouble()) / ln(2.0))
    }

    fun analyze(password: String): AnalysisResult {
        val feedback = mutableListOf<String>()
        val lowercase = password.lowercase()

        val isCommonWeak = WEAK_PASSWORDS.contains(lowercase)
        if (isCommonWeak) {
            feedback.add("This password is on the list of most commonly used passwords. It is extremely easy to guess.")
        }

        var containsPattern = false
        for (pattern in KEYBOARD_PATTERNS) {
            if (password.length >= 3) {
                for (i in 0..pattern.length - 3) {
                    val sub = pattern.substring(i, i + 3)
                    if (lowercase.contains(sub)) {
                        containsPattern = true
                        break
                    }
                }
            }
        }
        if (containsPattern) {
            feedback.add("Avoid sequential keyboard sequences like 'qwe', 'asd', or '123'.")
        }

        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        if (!hasUpper) feedback.add("Add uppercase letters to increase security.")
        if (!hasLower) feedback.add("Add lowercase letters.")
        if (!hasDigit) feedback.add("Add numbers.")
        if (!hasSpecial) feedback.add("Add special characters (e.g. !, @, #).")

        val length = password.length
        if (length < 8) {
            feedback.add("Password is too short. Try to make it at least 8 characters long.")
        } else if (length >= 14) {
            feedback.add("Great length! Longer passwords are exponentially harder to brute-force.")
        }

        // Shannon entropy of password
        val shannonEntropy = calculateShannonEntropy(password)
        val poolEntropy = calculatePoolEntropy(password)

        // Strength Grade determination logic based on poolEntropy, shannonEntropy, and dictionary checks
        val strengthGrade = when {
            isCommonWeak || length < 6 -> StrengthGrade.VERY_WEAK
            length < 8 || poolEntropy < 28 -> StrengthGrade.WEAK
            poolEntropy < 45 || shannonEntropy < 2.5 -> StrengthGrade.MEDIUM
            poolEntropy < 60 || containsPattern -> StrengthGrade.STRONG
            else -> StrengthGrade.VERY_STRONG
        }

        if (feedback.isEmpty() && strengthGrade == StrengthGrade.VERY_STRONG) {
            feedback.add("Excellent! This is a highly secure, high-entropy password.")
        }

        return AnalysisResult(
            entropy = shannonEntropy,
            length = length,
            hasUppercase = hasUpper,
            hasLowercase = hasLower,
            hasDigit = hasDigit,
            hasSpecial = hasSpecial,
            isCommonWeak = isCommonWeak,
            containsKeyboardPattern = containsPattern,
            strengthGrade = strengthGrade,
            feedback = feedback
        )
    }
}
