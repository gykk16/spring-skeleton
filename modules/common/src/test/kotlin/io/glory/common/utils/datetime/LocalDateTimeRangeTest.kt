package io.glory.common.utils.datetime

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime

@DisplayName("LocalDateTimeRange")
class LocalDateTimeRangeTest {

    @Nested
    @DisplayName("Creation")
    inner class CreationTest {

        @Test
        fun `should create range using from factory method`() {
            // given
            val start = LocalDateTime.of(2025, 1, 1, 10, 0)
            val end = LocalDateTime.of(2025, 12, 31, 18, 0)

            // when
            val range = LocalDateTimeRange.from(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range using invoke operator`() {
            // given
            val start = LocalDateTime.of(2025, 1, 1, 10, 0)
            val end = LocalDateTime.of(2025, 12, 31, 18, 0)

            // when
            val range = LocalDateTimeRange(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range when start equals end`() {
            // given
            val dateTime = LocalDateTime.of(2025, 6, 15, 12, 30)

            // when
            val range = LocalDateTimeRange.from(dateTime, dateTime)

            // then
            assertThat(range.start).isEqualTo(dateTime)
            assertThat(range.end).isEqualTo(dateTime)
        }

        @Test
        fun `should throw exception when start is after end`() {
            // given
            val start = LocalDateTime.of(2025, 12, 31, 18, 0)
            val end = LocalDateTime.of(2025, 1, 1, 10, 0)

            // when & then
            assertThatThrownBy { LocalDateTimeRange.from(start, end) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("start must be before end")
        }
    }

    @Nested
    @DisplayName("contains")
    inner class ContainsTest {

        @Test
        fun `should return true when datetime is within range`() {
            // given
            val range = LocalDateTimeRange.from(
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
            )
            val dateTimeInRange = LocalDateTime.of(2025, 6, 15, 12, 0)

            // when
            val result = range.contains(dateTimeInRange)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `should return true when datetime equals start or end`() {
            // given
            val start = LocalDateTime.of(2025, 1, 1, 10, 0)
            val end = LocalDateTime.of(2025, 12, 31, 18, 0)
            val range = LocalDateTimeRange.from(start, end)

            // when & then
            assertThat(range.contains(start)).isTrue()
            assertThat(range.contains(end)).isTrue()
        }

        @Test
        fun `should return false when datetime is outside range`() {
            // given
            val range = LocalDateTimeRange.from(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 12, 31, 18, 0)
            )
            val beforeRange = LocalDateTime.of(2025, 1, 1, 9, 59)
            val afterRange = LocalDateTime.of(2025, 12, 31, 18, 1)

            // when & then
            assertThat(range.contains(beforeRange)).isFalse()
            assertThat(range.contains(afterRange)).isFalse()
        }
    }

    @Nested
    @DisplayName("difference and time calculations")
    inner class DifferenceTest {

        @Test
        fun `should return correct duration between datetimes`() {
            // given
            val range = LocalDateTimeRange.from(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 30)
            )

            // when
            val duration = range.difference()

            // then
            assertThat(duration).isEqualTo(Duration.ofHours(2).plusMinutes(30))
        }

        @Test
        fun `should return correct days between datetimes`() {
            // given
            val range = LocalDateTimeRange.from(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 11, 10, 0)
            )

            // when
            val days = range.daysBetween()

            // then
            assertThat(days).isEqualTo(10L)
        }

        @Test
        fun `should return correct hours between datetimes`() {
            // given
            val range = LocalDateTimeRange.from(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 18, 30)
            )

            // when
            val hours = range.hoursBetween()

            // then
            assertThat(hours).isEqualTo(8L)
        }

        @Test
        fun `should return correct minutes between datetimes`() {
            // given
            val range = LocalDateTimeRange.from(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 10, 45)
            )

            // when
            val minutes = range.minutesBetween()

            // then
            assertThat(minutes).isEqualTo(45L)
        }

        @Test
        fun `should return zero when start equals end`() {
            // given
            val dateTime = LocalDateTime.of(2025, 6, 15, 12, 30)
            val range = LocalDateTimeRange.from(dateTime, dateTime)

            // when
            val duration = range.difference()
            val days = range.daysBetween()
            val hours = range.hoursBetween()
            val minutes = range.minutesBetween()

            // then
            assertThat(duration).isEqualTo(Duration.ZERO)
            assertThat(days).isEqualTo(0L)
            assertThat(hours).isEqualTo(0L)
            assertThat(minutes).isEqualTo(0L)
        }
    }
}
