package io.glory.common.utils.datetime

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

@JvmRecord
@ConsistentCopyVisibility
data class LocalDateRange private constructor(
    val start: LocalDate,
    val end: LocalDate,
) {

    init {
        require(!start.isAfter(end)) {
            "start must be before end, start: $start , end: $end"
        }
    }

    /**
     * @return a [LocalDateRange] with the specified [start] and [end] dates
     */
    companion object {
        @JvmStatic
        fun from(start: LocalDate, end: LocalDate): LocalDateRange {
            return LocalDateRange(start, end)
        }

        operator fun invoke(start: LocalDate, end: LocalDate): LocalDateRange {
            return from(start, end)
        }
    }

    /**
     * @return true if the specified [date] is within the range.
     * The start date and end date are included.
     */
    fun contains(date: LocalDate): Boolean {
        return !date.isBefore(start) && !date.isAfter(end)
    }

    /**
     * @return [Period] consisting of the number of years, months, days between two dates.
     * The start date is inclusive and the end date is exclusive
     */
    fun difference(): Period {
        return Period.between(start, end)
    }

    /**
     * @return the number of days between [start] and [end] dates
     */
    fun daysBetween(): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

}