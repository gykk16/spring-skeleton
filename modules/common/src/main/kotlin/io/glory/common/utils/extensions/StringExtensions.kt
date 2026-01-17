@file:JvmName("StringExt")

package io.glory.common.utils.extensions

private const val MASK_CHAR = "*"
private const val DEFAULT_MASK = "****"
private const val MASK_3 = "***"

// Cached Regex patterns for maskDefault (by string length)
private val MASK_PATTERN_5 = Regex(".(?=.)")
private val MASK_PATTERN_6 = Regex(".(?=.{2})")
private val MASK_PATTERN_7 = Regex(".(?=.{3})")
private val MASK_PATTERN_8 = Regex(".(?=.{4})")
private val MASK_PATTERN_9 = Regex("(?<=.).(?=.{4})")
private val MASK_PATTERN_10 = Regex("(?<=.{2}).(?=.{4})")
private val MASK_PATTERN_11 = Regex("(?<=.{3}).(?=.{4})")
private val MASK_PATTERN_12_PLUS = Regex("(?<=.{4}).(?=.{4})")

// Cached Regex pattern for maskKoreanName
private val MASK_PATTERN_KOREAN_NAME = Regex("(?<=.).(?=.)")

/**
 * @return the string itself if it's not null or blank; otherwise, returns the [default] value.
 */
@JvmOverloads
fun String?.ifNullOrBlank(default: String = ""): String {
    return if (this.isNullOrBlank()) default else this
}

/**
 * @return the string with all spaces removed.
 */
fun String.removeAllSpaces(): String = this.replace(" ", "")

/**
 * Masks the string based on its length.
 *
 * Masking rules by length:
 * - 1~4: Returns "****"
 * - 5: Shows last 1 char (e.g., "12345" -> "****5")
 * - 6: Shows last 2 chars (e.g., "123456" -> "****56")
 * - 7: Shows last 3 chars (e.g., "1234567" -> "****567")
 * - 8: Shows last 4 chars (e.g., "12345678" -> "****5678")
 * - 9: Shows first 1 + last 4 (e.g., "123456789" -> "1****6789")
 * - 10: Shows first 2 + last 4 (e.g., "1234567890" -> "12****7890")
 * - 11: Shows first 3 + last 4 (e.g., "12345678901" -> "123****8901")
 * - 12+: Shows first 4 + last 4 (e.g., "123456789012" -> "1234****9012")
 *
 * @param strict if true, throws [IllegalArgumentException] when input is blank.
 * @return masked string.
 */
@JvmOverloads
fun String.maskDefault(strict: Boolean = false): String {
    validateIfStrict(strict)

    if (length <= 4) return DEFAULT_MASK

    val pattern = when (length) {
        5 -> MASK_PATTERN_5
        6 -> MASK_PATTERN_6
        7 -> MASK_PATTERN_7
        8 -> MASK_PATTERN_8
        9 -> MASK_PATTERN_9
        10 -> MASK_PATTERN_10
        11 -> MASK_PATTERN_11
        else -> MASK_PATTERN_12_PLUS
    }

    return this.replace(pattern, MASK_CHAR)
}

/**
 * Masks Korean name.
 *
 * Masking rules:
 * - 0~1 chars: Returns "***"
 * - 2 chars: Shows first char + "*" (e.g., "홍길" -> "홍*")
 * - 3+ chars: Masks middle chars (e.g., "홍길동" -> "홍*동", "홍길동수" -> "홍**수")
 *
 * @param strict if true, throws [IllegalArgumentException] when input is blank.
 * @return masked name.
 */
@JvmOverloads
fun String.maskKoreanName(strict: Boolean = false): String {
    validateIfStrict(strict)

    return when (length) {
        in 0..1 -> MASK_3
        2 -> "${this[0]}$MASK_CHAR"
        else -> this.replace(MASK_PATTERN_KOREAN_NAME, MASK_CHAR)
    }
}

/**
 * Masks phone number by hiding all digits except last 4.
 *
 * @param strict if true, throws [IllegalArgumentException] when input is blank.
 * @return masked phone number (e.g., "01012345678" -> "*******5678").
 */
@JvmOverloads
fun String.maskPhone(strict: Boolean = false): String {
    validateIfStrict(strict)
    return mask(0)
}

/**
 * Masks email address.
 *
 * Masking rules:
 * - Shows first 2 chars of local part + "***" + "@" + full domain
 * - If local part <= 2 chars: "***@domain"
 * - If no valid @ found: falls back to [mask]
 *
 * @param strict if true, throws [IllegalArgumentException] when input is blank.
 * @return masked email (e.g., "user@example.com" -> "us***@example.com").
 */
@JvmOverloads
fun String.maskEmail(strict: Boolean = false): String {
    validateIfStrict(strict)

    if (length <= 4) return DEFAULT_MASK

    val atIndex = indexOf('@')
    if (atIndex <= 0 || atIndex >= length - 1) {
        return mask(0)
    }

    val localPart = substring(0, atIndex)
    val domainPart = substring(atIndex + 1)

    val maskedLocal = if (localPart.length <= 2) {
        MASK_3
    } else {
        val maskCount = (localPart.length - 2).coerceAtLeast(3)
        "${localPart[0]}${localPart[1]}${MASK_CHAR.repeat(maskCount)}"
    }

    return "$maskedLocal@$domainPart"
}

/**
 * Masks string between [start] and [end] indices.
 *
 * @param start starting index to mask (inclusive, default: 0).
 * @param end ending index to mask (exclusive, default: length - 4).
 * @return masked string (e.g., "1234567890".mask(2, 6) -> "12****7890").
 */
@JvmOverloads
fun String.mask(start: Int = 0, end: Int = this.length - 4): String {
    if (length <= 4) return DEFAULT_MASK

    val safeStart = start.coerceIn(0, length)
    val safeEnd = end.coerceIn(safeStart, length)

    if (safeStart >= safeEnd) return this

    return buildString(length) {
        append(this@mask, 0, safeStart)
        repeat(safeEnd - safeStart) { append(MASK_CHAR) }
        append(this@mask, safeEnd, this@mask.length)
    }
}

private fun String.validateIfStrict(strict: Boolean) {
    if (strict) {
        require(this.isNotBlank()) { "Input string cannot be blank" }
    }
}
