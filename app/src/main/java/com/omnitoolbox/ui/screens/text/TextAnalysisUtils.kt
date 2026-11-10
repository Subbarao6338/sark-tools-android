package omni.toolbox.ui.screens.text

import java.util.Locale

data class TextStats(
    val wordCount: Int,
    val charCount: Int,
    val charCountNoSpaces: Int,
    val sentenceCount: Int,
    val paragraphCount: Int,
    val readingTimeMinutes: Double,
    val speakingTimeMinutes: Double
)

enum class DiffType {
    ADDED,
    REMOVED,
    UNCHANGED
}

data class DiffLine(
    val type: DiffType,
    val text: String
)

object TextAnalysisUtils {

    fun calculateTextStats(text: String): TextStats {
        if (text.isBlank()) {
            return TextStats(
                wordCount = 0,
                charCount = text.length,
                charCountNoSpaces = text.count { !it.isWhitespace() },
                sentenceCount = 0,
                paragraphCount = 0,
                readingTimeMinutes = 0.0,
                speakingTimeMinutes = 0.0
            )
        }

        val words = text.trim().split("\\s+".toRegex())
        val wordCount = words.size
        val charCount = text.length
        val charCountNoSpaces = text.count { !it.isWhitespace() }
        val sentenceCount = text.split("[.!?]+".toRegex()).filter { it.isNotBlank() }.size
        val paragraphCount = text.split("\n+".toRegex()).filter { it.isNotBlank() }.size
        val readingTimeMinutes = wordCount / 200.0
        val speakingTimeMinutes = wordCount / 130.0

        return TextStats(
            wordCount = wordCount,
            charCount = charCount,
            charCountNoSpaces = charCountNoSpaces,
            sentenceCount = sentenceCount,
            paragraphCount = paragraphCount,
            readingTimeMinutes = readingTimeMinutes,
            speakingTimeMinutes = speakingTimeMinutes
        )
    }

    fun toUppercase(text: String): String {
        return text.uppercase(Locale.getDefault())
    }

    fun toLowercase(text: String): String {
        return text.lowercase(Locale.getDefault())
    }

    fun toTitleCase(text: String): String {
        if (text.isEmpty()) return text
        return text.split(" ").joinToString(" ") { word ->
            word.lowercase(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    fun toSentenceCase(text: String): String {
        if (text.isEmpty()) return text
        val sentences = text.split("(?<=[.!?])\\s+".toRegex())
        return sentences.joinToString(" ") { sentence ->
            sentence.lowercase(Locale.getDefault()).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }

    private fun extractWords(text: String): List<String> {
        val withSpaces = text.replace("(?<=[a-z0-9])(?=[A-Z])".toRegex(), " ")
            .replace("(?<=[A-Z])(?=[A-Z][a-z])".toRegex(), " ")
        return withSpaces.split("[^a-zA-Z0-9]+".toRegex()).filter { it.isNotEmpty() }
    }

    fun toCamelCase(text: String): String {
        val words = extractWords(text)
        if (words.isEmpty()) return ""
        return words.first().lowercase(Locale.getDefault()) +
                words.drop(1).joinToString("") { word ->
                    word.lowercase(Locale.getDefault()).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
                }
    }

    fun toPascalCase(text: String): String {
        val words = extractWords(text)
        if (words.isEmpty()) return ""
        return words.joinToString("") { word ->
            word.lowercase(Locale.getDefault()).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }

    fun toSnakeCase(text: String): String {
        val words = extractWords(text)
        if (words.isEmpty()) return ""
        return words.joinToString("_") { it.lowercase(Locale.getDefault()) }
    }

    fun toKebabCase(text: String): String {
        val words = extractWords(text)
        if (words.isEmpty()) return ""
        return words.joinToString("-") { it.lowercase(Locale.getDefault()) }
    }

    fun computeLineDiff(original: String, modified: String): List<DiffLine> {
        if (original.isEmpty() && modified.isEmpty()) return emptyList()

        val origLines = if (original.isEmpty()) emptyList() else original.lines()
        val modLines = if (modified.isEmpty()) emptyList() else modified.lines()

        val m = origLines.size
        val n = modLines.size
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 1..m) {
            for (j in 1..n) {
                if (origLines[i - 1] == modLines[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1
                } else {
                    dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }

        val result = mutableListOf<DiffLine>()
        var i = m
        var j = n
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && origLines[i - 1] == modLines[j - 1]) {
                result.add(0, DiffLine(DiffType.UNCHANGED, origLines[i - 1]))
                i--
                j--
            } else if (j > 0 && (i == 0 || dp[i][j - 1] >= dp[i - 1][j])) {
                result.add(0, DiffLine(DiffType.ADDED, modLines[j - 1]))
                j--
            } else if (i > 0 && (j == 0 || dp[i][j - 1] < dp[i - 1][j])) {
                result.add(0, DiffLine(DiffType.REMOVED, origLines[i - 1]))
                i--
            }
        }
        return result
    }
}
