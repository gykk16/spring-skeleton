@file:JvmName("MaskingExt")

package io.glory.common.utils.extensions

private const val MASK_CHAR = '*'
private const val DEFAULT_MASK = "****"

/**
 * Masks characters between [start] and [end] indices.
 *
 * @param start starting index to mask (inclusive, default: 0)
 * @param end ending index to mask (exclusive, default: length - 4)
 * @param maskChar character to use for masking (default: '*')
 * @return masked string
 *
 * Examples:
 * - "1234567890".mask() -> "******7890"
 * - "1234567890".mask(2, 6) -> "12****7890"
 * - "1234".mask() -> "****"
 */
@JvmOverloads
fun String.mask(start: Int = 0, end: Int = length - 4, maskChar: Char = MASK_CHAR): String {
    if (length <= 4) return DEFAULT_MASK

    val safeStart = start.coerceIn(0, length)
    val safeEnd = end.coerceIn(safeStart, length)

    if (safeStart >= safeEnd) return this

    return buildString(length) {
        append(this@mask, 0, safeStart)
        repeat(safeEnd - safeStart) { append(maskChar) }
        append(this@mask, safeEnd, this@mask.length)
    }
}

/**
 * Masks a name by hiding middle characters.
 *
 * @param maskChar character to use for masking (default: '*')
 * @return masked name
 *
 * Examples:
 * - "홍길동".maskName() -> "홍*동"
 * - "홍길동수".maskName() -> "홍**수"
 * - "John".maskName() -> "J**n"
 * - "AB".maskName() -> "A*"
 * - "A".maskName() -> "***"
 */
@JvmOverloads
fun String.maskName(maskChar: Char = MASK_CHAR): String = when (length) {
    0, 1 -> DEFAULT_MASK.take(3)
    2 -> "${first()}$maskChar"
    else -> "${first()}${maskChar.toString().repeat(length - 2)}${last()}"
}

/**
 * Masks digits while preserving non-digit characters (spaces, dashes, etc.).
 * Shows only the last N digits.
 *
 * @param visibleDigits number of digits to show at the end (default: 4)
 * @param maskChar character to use for masking (default: '*')
 * @return masked string with format preserved
 *
 * Examples:
 * - "+82 10-1234-5678".maskDigits() -> "+** **-****-5678"
 * - "010-1234-5678".maskDigits(4) -> "***-****-5678"
 * - "1234-5678-9012-3456".maskDigits(4) -> "****-****-****-3456"
 */
@JvmOverloads
fun String.maskDigits(visibleDigits: Int = 4, maskChar: Char = MASK_CHAR): String {
    val digitCount = count { it.isDigit() }
    if (digitCount <= visibleDigits) return this

    val digitsToMask = digitCount - visibleDigits
    var maskedCount = 0

    return map { char ->
        if (char.isDigit() && maskedCount < digitsToMask) {
            maskedCount++
            maskChar
        } else {
            char
        }
    }.joinToString("")
}

/**
 * Masks an email address by hiding part of the local part.
 *
 * @param visibleChars number of characters to show at start of local part (default: 2)
 * @param maskChar character to use for masking (default: '*')
 * @return masked email
 *
 * Examples:
 * - "user@example.com".maskEmail() -> "us**@example.com"
 * - "username@domain.co.kr".maskEmail() -> "us******@domain.co.kr"
 * - "ab@example.com".maskEmail() -> "ab@example.com" (too short to mask)
 */
@JvmOverloads
fun String.maskEmail(visibleChars: Int = 2, maskChar: Char = MASK_CHAR): String {
    val atIndex = indexOf('@')
    if (atIndex <= 0 || atIndex >= length - 1) return this

    val localPart = substring(0, atIndex)
    val domainPart = substring(atIndex + 1)

    if (localPart.length <= visibleChars) return this

    val maskedLocal = localPart.take(visibleChars) +
        maskChar.toString().repeat(localPart.length - visibleChars)

    return "$maskedLocal@$domainPart"
}
