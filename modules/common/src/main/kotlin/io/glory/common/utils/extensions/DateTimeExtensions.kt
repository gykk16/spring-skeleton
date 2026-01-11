@file:JvmName("DateTimeExt")

package io.glory.common.utils.extensions

import java.time.LocalDate
import java.time.Period

/**
 * @return true if this date is today
 */
fun LocalDate.isToday(): Boolean = equals(LocalDate.now())

/**
 * @return true if this date is yesterday
 */
fun LocalDate.isYesterday(): Boolean = equals(LocalDate.now().minusDays(1))

/**
 * @return true if this date is tomorrow
 */
fun LocalDate.isTomorrow(): Boolean = equals(LocalDate.now().plusDays(1))

/**
 * @return true if this date is in the past
 */
fun LocalDate.isPast(): Boolean = this < LocalDate.now()

/**
 * @return true if this date is in the future
 */
fun LocalDate.isFuture(): Boolean = this > LocalDate.now()

/**
 * @return age in years
 */
fun LocalDate.getAge(): Int = this.getAge(LocalDate.now())

/**
 * @param targetDate the date to compare with
 * @return age in years
 */
fun LocalDate.getAge(targetDate: LocalDate): Int {
//    var age = targetDate.year - this.year
//    if (targetDate < this.withYear(targetDate.year)) {
//        age--
//    }
    return Period.between(this, targetDate).years
}