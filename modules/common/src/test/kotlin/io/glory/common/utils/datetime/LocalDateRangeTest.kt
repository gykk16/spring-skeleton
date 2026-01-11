package io.glory.common.utils.datetime

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Period

@DisplayName("LocalDateRange")
class LocalDateRangeTest {

    @Nested
    @DisplayName("Creation")
    inner class CreationTest {

        @Test
        fun `should create range using from factory method`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 12, 31)

            // when
            val range = LocalDateRange.from(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range using invoke operator`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 12, 31)

            // when
            val range = LocalDateRange(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range when start equals end`() {
            // given
            val date = LocalDate.of(2025, 6, 15)

            // when
            val range = LocalDateRange.from(date, date)

            // then
            assertThat(range.start).isEqualTo(date)
            assertThat(range.end).isEqualTo(date)
        }

        @Test
        fun `should throw exception when start is after end`() {
            // given
            val start = LocalDate.of(2025, 12, 31)
            val end = LocalDate.of(2025, 1, 1)

            // when & then
            assertThatThrownBy { LocalDateRange.from(start, end) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("start must be before end")
        }
    }

    @Nested
    @DisplayName("contains")
    inner class ContainsTest {

        @Test
        fun `should return true when date is within range`() {
            // given
            val range = LocalDateRange.from(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
            val dateInRange = LocalDate.of(2025, 6, 15)

            // when
            val result = range.contains(dateInRange)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `should return true when date equals start or end`() {
            // given
            val start = LocalDate.of(2025, 1, 1)
            val end = LocalDate.of(2025, 12, 31)
            val range = LocalDateRange.from(start, end)

            // when & then
            assertThat(range.contains(start)).isTrue()
            assertThat(range.contains(end)).isTrue()
        }

        @Test
        fun `should return false when date is outside range`() {
            // given
            val range = LocalDateRange.from(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
            )
            val beforeRange = LocalDate.of(2024, 12, 31)
            val afterRange = LocalDate.of(2026, 1, 1)

            // when & then
            assertThat(range.contains(beforeRange)).isFalse()
            assertThat(range.contains(afterRange)).isFalse()
        }
    }

    @Nested
    @DisplayName("difference and daysBetween")
    inner class DifferenceTest {

        @Test
        fun `should return correct period between dates`() {
            // given
            val range = LocalDateRange.from(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 3, 15)
            )

            // when
            val period = range.difference()

            // then
            assertThat(period).isEqualTo(Period.of(0, 2, 14))
        }

        @Test
        fun `should return correct days between dates`() {
            // given
            val range = LocalDateRange.from(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 11)
            )

            // when
            val days = range.daysBetween()

            // then
            assertThat(days).isEqualTo(10L)
        }

        @Test
        fun `should return correct days when spanning across months`() {
            // given
            val rangeAcrossOneMonth = LocalDateRange.from(
                LocalDate.of(2025, 1, 25),
                LocalDate.of(2025, 2, 10)
            )
            val rangeAcrossMultipleMonths = LocalDateRange.from(
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 4, 20)
            )
            val rangeAcrossYears = LocalDateRange.from(
                LocalDate.of(2024, 12, 15),
                LocalDate.of(2025, 2, 10)
            )

            // when
            val daysAcrossOneMonth = rangeAcrossOneMonth.daysBetween()
            val daysAcrossMultipleMonths = rangeAcrossMultipleMonths.daysBetween()
            val daysAcrossYears = rangeAcrossYears.daysBetween()

            // then
            assertThat(daysAcrossOneMonth).isEqualTo(16L)
            assertThat(daysAcrossMultipleMonths).isEqualTo(95L)
            assertThat(daysAcrossYears).isEqualTo(57L)
        }

        @Test
        fun `should return zero when start equals end`() {
            // given
            val date = LocalDate.of(2025, 6, 15)
            val range = LocalDateRange.from(date, date)

            // when
            val period = range.difference()
            val days = range.daysBetween()

            // then
            assertThat(period).isEqualTo(Period.ZERO)
            assertThat(days).isEqualTo(0L)
        }
    }
}
