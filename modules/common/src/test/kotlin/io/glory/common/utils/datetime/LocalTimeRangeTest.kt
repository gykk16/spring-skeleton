package io.glory.common.utils.datetime

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalTime

@DisplayName("LocalTimeRange")
class LocalTimeRangeTest {

    @Nested
    @DisplayName("Creation")
    inner class CreationTest {

        @Test
        fun `should create range using from factory method`() {
            // given
            val start = LocalTime.of(9, 0)
            val end = LocalTime.of(18, 0)

            // when
            val range = LocalTimeRange.from(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range using invoke operator`() {
            // given
            val start = LocalTime.of(9, 0)
            val end = LocalTime.of(18, 0)

            // when
            val range = LocalTimeRange(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range when start equals end`() {
            // given
            val time = LocalTime.of(12, 30)

            // when
            val range = LocalTimeRange.from(time, time)

            // then
            assertThat(range.start).isEqualTo(time)
            assertThat(range.end).isEqualTo(time)
        }

        @Test
        fun `should throw exception when start is after end`() {
            // given
            val start = LocalTime.of(18, 0)
            val end = LocalTime.of(9, 0)

            // when & then
            assertThatThrownBy { LocalTimeRange.from(start, end) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("start must be before end")
        }
    }

    @Nested
    @DisplayName("contains")
    inner class ContainsTest {

        @Test
        fun `should return true when time is within range`() {
            // given
            val range = LocalTimeRange.from(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
            )
            val timeInRange = LocalTime.of(12, 30)

            // when
            val result = range.contains(timeInRange)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `should return true when time equals start or end`() {
            // given
            val start = LocalTime.of(9, 0)
            val end = LocalTime.of(18, 0)
            val range = LocalTimeRange.from(start, end)

            // when & then
            assertThat(range.contains(start)).isTrue()
            assertThat(range.contains(end)).isTrue()
        }

        @Test
        fun `should return false when time is outside range`() {
            // given
            val range = LocalTimeRange.from(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
            )
            val beforeRange = LocalTime.of(8, 59)
            val afterRange = LocalTime.of(18, 1)

            // when & then
            assertThat(range.contains(beforeRange)).isFalse()
            assertThat(range.contains(afterRange)).isFalse()
        }
    }

    @Nested
    @DisplayName("difference and time calculations")
    inner class DifferenceTest {

        @Test
        fun `should return correct duration between times`() {
            // given
            val range = LocalTimeRange.from(
                LocalTime.of(10, 0),
                LocalTime.of(12, 30)
            )

            // when
            val duration = range.difference()

            // then
            assertThat(duration).isEqualTo(Duration.ofHours(2).plusMinutes(30))
        }

        @Test
        fun `should return correct hours between times`() {
            // given
            val range = LocalTimeRange.from(
                LocalTime.of(9, 0),
                LocalTime.of(18, 30)
            )

            // when
            val hours = range.hoursBetween()

            // then
            assertThat(hours).isEqualTo(9L)
        }

        @Test
        fun `should return correct minutes between times`() {
            // given
            val range = LocalTimeRange.from(
                LocalTime.of(10, 0),
                LocalTime.of(10, 45)
            )

            // when
            val minutes = range.minutesBetween()

            // then
            assertThat(minutes).isEqualTo(45L)
        }

        @Test
        fun `should return zero when start equals end`() {
            // given
            val time = LocalTime.of(12, 30)
            val range = LocalTimeRange.from(time, time)

            // when
            val duration = range.difference()
            val hours = range.hoursBetween()
            val minutes = range.minutesBetween()

            // then
            assertThat(duration).isEqualTo(Duration.ZERO)
            assertThat(hours).isEqualTo(0L)
            assertThat(minutes).isEqualTo(0L)
        }
    }
}
