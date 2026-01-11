package io.glory.common.utils.datetime

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Period

@DisplayName("SearchDates")
class SearchDatesTest {

    @Nested
    @DisplayName("Creation with default values")
    inner class DefaultCreationTest {

        @Test
        fun `should create with default values`() {
            // given
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            // when
            val searchDates = SearchDates()

            // then
            assertThat(searchDates.startDate).isEqualTo(yesterday)
            assertThat(searchDates.endDate).isEqualTo(today)
            assertThat(searchDates.strict).isFalse()
            assertThat(searchDates.searchPeriod).isEqualTo(Period.ofDays(1))
            assertThat(searchDates.maxSearchPeriod).isEqualTo(Period.ofMonths(1))
        }

        @Test
        fun `should create with custom start and end dates`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 1, 15)

            // when
            val searchDates = SearchDates(startDate = start, endDate = end)

            // then
            assertThat(searchDates.startDate).isEqualTo(start)
            assertThat(searchDates.endDate).isEqualTo(end)
        }
    }

    @Nested
    @DisplayName("Validation with strict mode disabled")
    inner class NonStrictModeTest {

        @Test
        fun `should adjust startDate when start is after end in non-strict mode`() {
            // given
            val start = LocalDate.of(2025, 1, 15)
            val end = LocalDate.of(2025, 1, 1)

            // when
            val searchDates = SearchDates(startDate = start, endDate = end, strict = false)

            // then
            assertThat(searchDates.startDate).isEqualTo(end.minus(Period.ofDays(1)))
            assertThat(searchDates.endDate).isEqualTo(end)
        }

        @Test
        fun `should adjust startDate when search period exceeds max in non-strict mode`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 3, 15)

            // when
            val searchDates = SearchDates(startDate = start, endDate = end, strict = false)

            // then
            assertThat(searchDates.startDate).isEqualTo(end.minus(Period.ofMonths(1)))
            assertThat(searchDates.endDate).isEqualTo(end)
        }

        @Test
        fun `should use custom searchPeriod for adjustment`() {
            // given
            val start = LocalDate.of(2025, 1, 15)
            val end = LocalDate.of(2025, 1, 1)
            val customSearchPeriod = Period.ofDays(7)

            // when
            val searchDates = SearchDates(
                startDate = start,
                endDate = end,
                strict = false,
                searchPeriod = customSearchPeriod
            )

            // then
            assertThat(searchDates.startDate).isEqualTo(end.minus(customSearchPeriod))
        }

        @Test
        fun `should use custom maxSearchPeriod for validation`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 1, 20)
            val customMaxSearchPeriod = Period.ofDays(7)

            // when
            val searchDates = SearchDates(
                startDate = start,
                endDate = end,
                strict = false,
                maxSearchPeriod = customMaxSearchPeriod
            )

            // then
            assertThat(searchDates.startDate).isEqualTo(end.minus(customMaxSearchPeriod))
        }
    }

    @Nested
    @DisplayName("Validation with strict mode enabled")
    inner class StrictModeTest {

        @Test
        fun `should throw exception when start is after end in strict mode`() {
            // given
            val start = LocalDate.of(2025, 1, 15)
            val end = LocalDate.of(2025, 1, 1)

            // when & then
            assertThatThrownBy { SearchDates(startDate = start, endDate = end, strict = true) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("startDate must be before endDate")
        }

        @Test
        fun `should throw exception when search period exceeds max in strict mode`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 3, 15)

            // when & then
            assertThatThrownBy { SearchDates(startDate = start, endDate = end, strict = true) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Search period must not exceed")
        }

        @Test
        fun `should create successfully with valid dates in strict mode`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 1, 15)

            // when
            val searchDates = SearchDates(startDate = start, endDate = end, strict = true)

            // then
            assertThat(searchDates.startDate).isEqualTo(start)
            assertThat(searchDates.endDate).isEqualTo(end)
        }
    }

    @Nested
    @DisplayName("difference and searchDays")
    inner class DifferenceTest {

        @Test
        fun `should return correct period between dates`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 1, 15)
            val searchDates = SearchDates(startDate = start, endDate = end)

            // when
            val period = searchDates.difference()

            // then
            assertThat(period).isEqualTo(Period.ofDays(14))
        }

        @Test
        fun `should return correct search days`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 1, 11)
            val searchDates = SearchDates(startDate = start, endDate = end)

            // when
            val days = searchDates.searchDays()

            // then
            assertThat(days).isEqualTo(10L)
        }

        @Test
        fun `should return zero when start equals end`() {
            // given
            val date = LocalDate.of(2025, 1, 15)
            val searchDates = SearchDates(startDate = date, endDate = date)

            // when
            val period = searchDates.difference()
            val days = searchDates.searchDays()

            // then
            assertThat(period).isEqualTo(Period.ZERO)
            assertThat(days).isEqualTo(0L)
        }
    }
}
