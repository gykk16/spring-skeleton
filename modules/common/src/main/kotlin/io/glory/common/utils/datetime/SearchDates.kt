package io.glory.common.utils.datetime

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

private val DEFAULT_SEARCH_PERIOD: Period = Period.ofDays(1)
private val DEFAULT_MAX_SEARCH_PERIOD: Period = Period.ofMonths(1)
private val REFERENCE_DATE: LocalDate = LocalDate.of(2023, 1, 1)

data class SearchDates @JvmOverloads constructor(
    var startDate: LocalDate = LocalDate.now().minus(DEFAULT_SEARCH_PERIOD),
    var endDate: LocalDate = LocalDate.now(),
    val strict: Boolean = false,
    val searchPeriod: Period = DEFAULT_SEARCH_PERIOD,
    val maxSearchPeriod: Period = DEFAULT_MAX_SEARCH_PERIOD,
) {

    init {
        if (startDate.isAfter(endDate)) {
            require(!strict) { "startDate must be before endDate" }
            startDate = endDate.minus(searchPeriod)
        }

        val maxSearchDays = ChronoUnit.DAYS.between(
            REFERENCE_DATE, REFERENCE_DATE.plus(maxSearchPeriod)
        )
        val searchDays = ChronoUnit.DAYS.between(startDate, endDate)
        if (searchDays > maxSearchDays) {
            require(!strict) { "Search period must not exceed $maxSearchPeriod" }
            startDate = endDate.minus(maxSearchPeriod)
        }
    }

    /**
     * @return [Period] consisting of the number of years, months, days between two dates.
     * The start date is inclusive and the end date is exclusive
     */
    fun difference(): Period {
        return Period.between(startDate, endDate)
    }

    /**
     * @return the number of days between [startDate] and [endDate] dates
     */
    fun searchDays(): Long {
        return ChronoUnit.DAYS.between(startDate, endDate)
    }

}