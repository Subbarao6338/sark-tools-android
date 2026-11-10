package omni.toolbox.ui.screens.text

object MorseUtils {
    val charToMorseMap = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.",
        'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..",
        'M' to "--", 'N' to "-.", 'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.",
        'S' to "...", 'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..", '0' to "-----", '1' to ".----", '2' to "..---",
        '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----.", ' ' to "/"
    )

    val morseToCharMap: Map<String, String> = charToMorseMap.entries
        .filter { it.key != ' ' }
        .associate { (k, v) -> v to k.toString() } + mapOf("/" to " ")

    fun encode(text: String): String {
        if (text.isEmpty()) return ""
        return text.uppercase().map { charToMorseMap[it] ?: "?" }.joinToString(" ")
    }

    fun decode(morseCode: String): String {
        if (morseCode.trim().isEmpty()) return ""
        return morseCode.trim().split(Regex("\\s+")).map { code ->
            morseToCharMap[code] ?: "?"
        }.joinToString("")
    }
}
