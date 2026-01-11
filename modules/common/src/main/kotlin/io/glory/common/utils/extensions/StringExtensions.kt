@file:JvmName("StringExt")

package io.glory.common.utils.extensions

private const val MASK_STAR = "*"

/**
 * @return the string itself if it's not null or blank; otherwise, returns the [default] value.
 */
@JvmOverloads
fun String?.ifNullOrBlank(default: String = ""): String {
    return if (this.isNullOrBlank()) default else this
}

/**
 * @return the string with all spaces removed
 */
fun String.removeAllSpaces(): String = this.replace(" ", "")

/**
 * @return masked string from the string. If [strict] is true, the input string cannot be empty.
 */
@JvmOverloads
fun String.maskDefault(strict: Boolean = false): String {
    if (strict) {
        require(this.isNotBlank()) { "Input string cannot be blank" }
    }

    val maskPattern = when (length) {
        5 -> ".(?=.)"
        6 -> ".(?=.{2})"
        7 -> ".(?=.{3})"
        8 -> ".(?=.{4})"
        9 -> "(?<=.).(?=.{4})"
        10 -> "(?<=.{2}).(?=.{4})"
        11 -> "(?<=.{3}).(?=.{4})"
        else -> "(?<=.{4}).(?=.{4})"
    }

    return if (length <= 4) {
        "****"
    } else {
        this.replace(Regex(maskPattern), MASK_STAR)
    }
}

/**
 * @return masked string from the string. If [strict] is true, the input string cannot be empty.
 */
@JvmOverloads
fun String.maskKoreanName(strict: Boolean = false): String {
    if (strict) {
        require(this.isNotBlank()) { "Input string cannot be blank" }
    }

    return when (length) {
        in 0..1 -> MASK_STAR.repeat(3)
        2 -> this[0] + MASK_STAR
        else -> this.replace(Regex("(?<=.).(?=.)"), MASK_STAR)
    }
}

/**
 * @return masked string from the string. If [strict] is true, the input string cannot be empty.
 */
@JvmOverloads
fun String.maskPhone(strict: Boolean = false): String {
    if (strict) {
        require(this.isNotBlank()) { "Input string cannot be blank" }
    }
    return mask(0)
}

/**
 * @return masked email from the string. If [strict] is true, the input string cannot be empty.
 */
@JvmOverloads
fun String.maskEmail(strict: Boolean = false): String {
    if (strict) {
        require(this.isNotBlank()) { "Input string cannot be blank" }
    }

    if (length <= 4) {
        return "****"
    }

    val atIndex = indexOf('@')

    // If @ is found and there's content before and after it
    if (atIndex > 0 && atIndex < length - 1) {
        val localPart = substring(0, atIndex)
        val domainPart = substring(atIndex + 1)

        val maskedLocal = when {
            localPart.length <= 2 -> MASK_STAR.repeat(3)
            else -> localPart.take(2) + MASK_STAR.repeat((localPart.length - 2).coerceAtLeast(3))
        }

        return "$maskedLocal@$domainPart"
    }

    return mask(0)
}

/**
 * @return masked string from the string between [start] and [end] indices.
 */
fun String.mask(start: Int = 0, end: Int = this.length - 4): String {

    if (length <= 4) {
        return "****"
    }

    val safeStart = start.coerceIn(0, length)
    val safeEnd = end.coerceIn(safeStart, length)

    if (safeStart >= safeEnd) {
        return this
    }

    return buildString {
        append(this@mask.substring(0, safeStart))
        append(MASK_STAR.repeat(safeEnd - safeStart))
        append(this@mask.substring(safeEnd))
    }

}

