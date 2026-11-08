package com.toolbox.conversion.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "",
    val fractionResult: String = "",
    val showFraction: Boolean = false,
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,
    val useDegrees: Boolean = true,
    val history: List<Pair<String, String>> = emptyList(),
)

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    fun onInput(token: String) {
        _state.update { it.copy(expression = it.expression + token) }
        tryEvaluate()
    }

    fun onClear() {
        _state.update { it.copy(expression = "", result = "", fractionResult = "") }
    }

    fun onBackspace() {
        _state.update {
            val expr = it.expression
            if (expr.isEmpty()) return@update it
            // Remove trailing function name if present
            val functions = listOf(
                "sinh(", "cosh(", "tanh(", "asin(", "acos(", "atan(",
                "cbrt(", "sqrt(", "abs(", "round(", "floor(", "ceil(",
                "sin(", "cos(", "tan(", "ln(", "log(", "exp("
            )
            val matchedFunc = functions.firstOrNull { f -> expr.endsWith(f) }
            val newExpr = if (matchedFunc != null) {
                expr.dropLast(matchedFunc.length)
            } else {
                expr.dropLast(1)
            }
            it.copy(expression = newExpr)
        }
        tryEvaluate()
    }

    fun onEquals() {
        val current = _state.value
        if (current.result.isNotEmpty() && !current.result.startsWith("Error")) {
            _state.update {
                it.copy(
                    history = (listOf(it.expression to it.result) + it.history).take(30),
                    expression = it.result,
                    result = "",
                )
            }
            tryEvaluate()
        }
    }

    fun toggleFraction() {
        _state.update { it.copy(showFraction = !it.showFraction) }
    }

    fun memoryClear() {
        _state.update { it.copy(memoryValue = 0.0, hasMemory = false) }
    }

    fun memoryRecall() {
        val mem = _state.value.memoryValue
        val memStr = if (mem == mem.toLong().toDouble()) mem.toLong().toString() else mem.toString()
        onInput(memStr)
    }

    fun memoryAdd() {
        val currentNum = _state.value.result.toDoubleOrNull()
            ?: _state.value.expression.toDoubleOrNull()
            ?: 0.0
        _state.update {
            val newMem = it.memoryValue + currentNum
            it.copy(memoryValue = newMem, hasMemory = true)
        }
    }

    fun memorySubtract() {
        val currentNum = _state.value.result.toDoubleOrNull()
            ?: _state.value.expression.toDoubleOrNull()
            ?: 0.0
        _state.update {
            val newMem = it.memoryValue - currentNum
            it.copy(memoryValue = newMem, hasMemory = true)
        }
    }

    fun onNegate() {
        _state.update {
            val expr = it.expression
            if (expr.isEmpty()) return@update it
            if (expr.startsWith("-")) {
                it.copy(expression = expr.drop(1))
            } else {
                it.copy(expression = "-$expr")
            }
        }
        tryEvaluate()
    }

    fun toggleAngleMode() {
        _state.update { it.copy(useDegrees = !it.useDegrees) }
        tryEvaluate()
    }

    fun onHistorySelect(expression: String) {
        _state.update { it.copy(expression = expression) }
        tryEvaluate()
    }

    fun clearHistory() {
        _state.update { it.copy(history = emptyList()) }
    }

    private fun tryEvaluate() {
        val expr = _state.value.expression
        if (expr.isBlank()) {
            _state.update { it.copy(result = "", fractionResult = "") }
            return
        }
        try {
            val result = evaluate(expr, _state.value.useDegrees)
            if (result.isNaN()) {
                _state.update { it.copy(result = "NaN", fractionResult = "") }
                return
            }
            if (result.isInfinite()) {
                _state.update { it.copy(result = if (result > 0) "∞" else "-∞", fractionResult = "") }
                return
            }
            val formatted = if (result == result.toLong().toDouble() && abs(result) < 1e14) {
                result.toLong().toString()
            } else {
                "%.10f".format(result).trimEnd('0').trimEnd('.')
            }
            val frac = toFraction(result)
            _state.update { it.copy(result = formatted, fractionResult = frac) }
        } catch (_: Exception) {
            _state.update { it.copy(result = "", fractionResult = "") }
        }
    }

    private fun toFraction(valIn: Double, maxDenominator: Long = 1000L): String {
        if (valIn.isNaN() || valIn.isInfinite() || abs(valIn) > 1e7) return ""
        if (valIn == 0.0) return "0"
        val sign = if (valIn < 0) "-" else ""
        val v = abs(valIn)
        val whole = v.toLong()
        val frac = v - whole
        if (frac < 1e-9) return "$sign$whole"

        var p0 = 0L
        var q0 = 1L
        var p1 = 1L
        var q1 = 0L
        var x = frac
        while (true) {
            val a = x.toLong()
            val p2 = a * p1 + p0
            val q2 = a * q1 + q0
            if (q2 > maxDenominator) break
            p0 = p1
            q0 = q1
            p1 = p2
            q1 = q2
            val diff = x - a
            if (abs(diff) < 1e-9) break
            x = 1.0 / diff
        }
        if (q1 == 0L || q1 == 1L) return "$sign${whole * q1 + p1}"
        val num = whole * q1 + p1
        val den = q1
        return "$sign$num/$den"
    }

    private fun evaluate(expr: String, useDegrees: Boolean): Double {
        val parser = ExpressionParser(expr, useDegrees)
        return parser.parse()
    }
}

/**
 * Recursive descent parser for mathematical expressions.
 * Supports: +, -, *, /, ^, %, permutations (P), combinations (C), parentheses, whitespace, and scientific functions.
 */
class ExpressionParser(private val rawInput: String, private val useDegrees: Boolean) {
    // Strip whitespace so expressions like "10 + 20 * 5" parse seamlessly
    private val input: String = rawInput.replace(" ", "")
    private var pos = 0

    fun parse(): Double {
        val result = parseExpression()
        if (pos < input.length) throw IllegalArgumentException("Unexpected: ${input[pos]}")
        return result
    }

    private fun parseExpression(): Double {
        var result = parseTerm()
        while (pos < input.length) {
            when {
                peek('+') -> { advance(); result += parseTerm() }
                peek('-') -> { advance(); result -= parseTerm() }
                else -> break
            }
        }
        return result
    }

    private fun parseTerm(): Double {
        var result = parseExponent()
        while (pos < input.length) {
            when {
                peek('×') || peek('*') -> { advance(); result *= parseExponent() }
                peek('÷') || peek('/') -> { advance(); result /= parseExponent() }
                peek('%') -> { advance(); result %= parseExponent() }
                peek('P') || peek('p') -> { advance(); result = nPr(result.toLong(), parseExponent().toLong()).toDouble() }
                peek('C') || peek('c') -> { advance(); result = nCr(result.toLong(), parseExponent().toLong()).toDouble() }
                else -> break
            }
        }
        return result
    }

    private fun parseExponent(): Double {
        var result = parseUnary()
        while (pos < input.length && peek('^')) {
            advance()
            result = result.pow(parseUnary())
        }
        return result
    }

    private fun parseUnary(): Double {
        if (peek('-')) {
            advance()
            return -parsePrimary()
        }
        if (peek('+')) {
            advance()
        }
        return parsePrimary()
    }

    private fun parsePrimary(): Double {
        // Functions
        val functions = listOf(
            "sinh", "cosh", "tanh",
            "asin", "acos", "atan",
            "cbrt", "sqrt", "abs", "round", "floor", "ceil",
            "sin", "cos", "tan", "ln", "log", "exp"
        )
        for (func in functions) {
            if (matchString(func)) {
                expect('(')
                val arg = parseExpression()
                expect(')')
                return maybeFactorial(applyFunction(func, arg))
            }
        }

        // Constants
        if (matchString("π") || matchString("pi")) return maybeFactorial(PI)
        if (matchString("e") && !peekDigit()) return maybeFactorial(E)

        // Parenthesized expression
        if (peek('(')) {
            advance()
            val result = parseExpression()
            expect(')')
            return maybeFactorial(result)
        }

        // Number
        val start = pos
        while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
        if (pos == start) throw IllegalArgumentException("Expected number at position $pos in '$input'")
        val number = input.substring(start, pos).toDouble()
        return maybeFactorial(number)
    }

    private fun maybeFactorial(value: Double): Double {
        if (pos < input.length && input[pos] == '!') {
            advance()
            return factorial(value.toLong()).toDouble()
        }
        return value
    }

    private fun applyFunction(name: String, arg: Double): Double {
        return when (name) {
            "sin" -> sin(toRadians(arg))
            "cos" -> cos(toRadians(arg))
            "tan" -> tan(toRadians(arg))
            "asin" -> fromRadians(asin(arg))
            "acos" -> fromRadians(acos(arg))
            "atan" -> fromRadians(atan(arg))
            "sinh" -> sinh(arg)
            "cosh" -> cosh(arg)
            "tanh" -> tanh(arg)
            "ln" -> ln(arg)
            "log" -> log10(arg)
            "exp" -> exp(arg)
            "sqrt" -> sqrt(arg)
            "cbrt" -> Math.cbrt(arg)
            "abs" -> abs(arg)
            "round" -> round(arg)
            "floor" -> floor(arg)
            "ceil" -> ceil(arg)
            else -> throw IllegalArgumentException("Unknown function: $name")
        }
    }

    private fun toRadians(value: Double): Double =
        if (useDegrees) Math.toRadians(value) else value

    private fun fromRadians(value: Double): Double =
        if (useDegrees) Math.toDegrees(value) else value

    private fun factorial(n: Long): Long {
        if (n < 0) throw IllegalArgumentException("Negative factorial")
        if (n <= 1) return 1
        var result = 1L
        for (i in 2..n.coerceAtMost(20)) result *= i
        return result
    }

    private fun nPr(n: Long, r: Long): Long {
        if (n < 0 || r < 0 || r > n) return 0
        var res = 1L
        for (i in 0 until r) {
            res *= (n - i)
        }
        return res
    }

    private fun nCr(n: Long, r: Long): Long {
        if (n < 0 || r < 0 || r > n) return 0
        val k = if (r > n - r) n - r else r
        var res = 1L
        for (i in 1..k) {
            res = res * (n - i + 1) / i
        }
        return res
    }

    private fun peek(c: Char): Boolean = pos < input.length && input[pos] == c
    private fun peekDigit(): Boolean = pos < input.length && input[pos].isDigit()
    private fun advance() { pos++ }
    private fun expect(c: Char) {
        if (!peek(c)) throw IllegalArgumentException("Expected '$c'")
        advance()
    }

    private fun matchString(s: String): Boolean {
        if (input.startsWith(s, pos)) {
            pos += s.length
            return true
        }
        return false
    }
}
