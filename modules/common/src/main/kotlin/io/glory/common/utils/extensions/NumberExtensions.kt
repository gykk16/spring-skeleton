@file:JvmName("NumberExt")

package io.glory.common.utils.extensions

import kotlin.math.pow
import kotlin.random.Random

/**
 * @return 0 or 1 randomly
 */
fun Number.zeroOrOne(): Int = Random.nextInt(2)

/**
 * @return 0 or 1 or 2 randomly
 */
fun Number.zeroOrOneOrTwo(): Int = Random.nextInt(3)

/**
 * @return random integer between [min] and [max] inclusive
 */
fun Number.randomBetween(min: Int, max: Int): Int = Random.nextInt((max - min) + 1) + min

/**
 * @param length 1 to 10
 * @return random integer of [length]
 */
fun Number.randomOfLength(length: Int): Long {
    require(length in 1..10) { "Length must be between 1 and 10." }
    val minValue = 10.0.pow(length - 1).toLong()
    val maxValue = 10.0.pow(length).toLong() - 1
    return Random.nextLong(minValue, maxValue + 1)
}

/**
 * Formats the number with comma separators and specified decimal places.
 *
 * @param position the number of decimal places to display
 * @return the formatted string with comma separators
 */
fun Number.commaSeparated(position: Int = 0): String {
    require(position >= 0) { "Position must be non-negative." }
    val format = "%,.${position}f"
    return String.format(format, this.toDouble())
}
