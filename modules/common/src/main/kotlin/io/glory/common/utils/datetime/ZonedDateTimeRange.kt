package io.glory.common.utils.datetime

import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@JvmRecord
@ConsistentCopyVisibility
data class ZonedDateTimeRange private constructor(
    val start: ZonedDateTime,
    val end: ZonedDateTime,
) {

    init {
        require(!start.isAfter(end)) {
            "start must be before end, start: $start , end: $end"
        }
    }

    /**
     * @return a [ZonedDateTimeRange] with the specified [start] and [end] datetime
     */
    companion object {
        @JvmStatic
        fun from(start: ZonedDateTime, end: ZonedDateTime): ZonedDateTimeRange {
            return ZonedDateTimeRange(start, end)
        }

        operator fun invoke(start: ZonedDateTime, end: ZonedDateTime): ZonedDateTimeRange {
            return from(start, end)
        }
    }

    /**
     * @return true if the specified [dateTime] is within the range.
     * The start date and end date are included.
     */
    fun contains(dateTime: ZonedDateTime): Boolean {
        return !dateTime.isBefore(start) && !dateTime.isAfter(end)
    }

    /**
     * @return [Duration] consisting of the number of years, months, days between two datetime.
     * The start date is inclusive and the end date is exclusive
     */
    fun difference(): Duration {
        return Duration.between(start, end)
    }

    /**
     * @return the number of days between [start] and [end] dates
     */
    fun daysBetween(): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

    /**
     * @return the number of hours between [start] and [end] dates
     */
    fun hoursBetween(): Long {
        return ChronoUnit.HOURS.between(start, end)
    }

    /**
     * @return the number of minutes between [start] and [end] dates
     */
    fun minutesBetween(): Long {
        return ChronoUnit.MINUTES.between(start, end)
    }

    /**
     * @return the time offset between the start and end dates
     */
    fun timeOffset(): ZoneOffset {
        return ZoneOffset.ofTotalSeconds(end.offset.totalSeconds - start.offset.totalSeconds)
    }

    /**
     * @return the [LocalDateTimeRange] representation of this [ZonedDateTimeRange]
     */
    fun toLocalDateTimeRange(): LocalDateTimeRange {
        return LocalDateTimeRange.from(start.toLocalDateTime(), end.toLocalDateTime())
    }

}