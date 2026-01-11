package io.glory.common.utils.datetime

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.*

@DisplayName("ZonedDateTimeRange")
class ZonedDateTimeRangeTest {

    private val seoulZone = ZoneId.of("Asia/Seoul")
    private val utcZone = ZoneId.of("UTC")

    @Nested
    @DisplayName("Creation")
    inner class CreationTest {

        @Test
        fun `should create range using from factory method`() {
            // given
            val start = ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone)
            val end = ZonedDateTime.of(2025, 12, 31, 18, 0, 0, 0, seoulZone)

            // when
            val range = ZonedDateTimeRange.from(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range using invoke operator`() {
            // given
            val start = ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone)
            val end = ZonedDateTime.of(2025, 12, 31, 18, 0, 0, 0, seoulZone)

            // when
            val range = ZonedDateTimeRange(start, end)

            // then
            assertThat(range.start).isEqualTo(start)
            assertThat(range.end).isEqualTo(end)
        }

        @Test
        fun `should create range when start equals end`() {
            // given
            val dateTime = ZonedDateTime.of(2025, 6, 15, 12, 30, 0, 0, seoulZone)

            // when
            val range = ZonedDateTimeRange.from(dateTime, dateTime)

            // then
            assertThat(range.start).isEqualTo(dateTime)
            assertThat(range.end).isEqualTo(dateTime)
        }

        @Test
        fun `should throw exception when start is after end`() {
            // given
            val start = ZonedDateTime.of(2025, 12, 31, 18, 0, 0, 0, seoulZone)
            val end = ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone)

            // when & then
            assertThatThrownBy { ZonedDateTimeRange.from(start, end) }
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
            val range = ZonedDateTimeRange.from(
                ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, seoulZone),
                ZonedDateTime.of(2025, 12, 31, 23, 59, 0, 0, seoulZone)
            )
            val dateTimeInRange = ZonedDateTime.of(2025, 6, 15, 12, 0, 0, 0, seoulZone)

            // when
            val result = range.contains(dateTimeInRange)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `should return true when datetime equals start or end`() {
            // given
            val start = ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone)
            val end = ZonedDateTime.of(2025, 12, 31, 18, 0, 0, 0, seoulZone)
            val range = ZonedDateTimeRange.from(start, end)

            // when & then
            assertThat(range.contains(start)).isTrue()
            assertThat(range.contains(end)).isTrue()
        }

        @Test
        fun `should return false when datetime is outside range`() {
            // given
            val range = ZonedDateTimeRange.from(
                ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone),
                ZonedDateTime.of(2025, 12, 31, 18, 0, 0, 0, seoulZone)
            )
            val beforeRange = ZonedDateTime.of(2025, 1, 1, 9, 59, 0, 0, seoulZone)
            val afterRange = ZonedDateTime.of(2025, 12, 31, 18, 1, 0, 0, seoulZone)

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
            val range = ZonedDateTimeRange.from(
                ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone),
                ZonedDateTime.of(2025, 1, 1, 12, 30, 0, 0, seoulZone)
            )

            // when
            val duration = range.difference()

            // then
            assertThat(duration).isEqualTo(Duration.ofHours(2).plusMinutes(30))
        }

        @Test
        fun `should return correct days between datetimes`() {
            // given
            val range = ZonedDateTimeRange.from(
                ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone),
                ZonedDateTime.of(2025, 1, 11, 10, 0, 0, 0, seoulZone)
            )

            // when
            val days = range.daysBetween()

            // then
            assertThat(days).isEqualTo(10L)
        }

        @Test
        fun `should return correct hours between datetimes`() {
            // given
            val range = ZonedDateTimeRange.from(
                ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone),
                ZonedDateTime.of(2025, 1, 1, 18, 30, 0, 0, seoulZone)
            )

            // when
            val hours = range.hoursBetween()

            // then
            assertThat(hours).isEqualTo(8L)
        }

        @Test
        fun `should return correct minutes between datetimes`() {
            // given
            val range = ZonedDateTimeRange.from(
                ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone),
                ZonedDateTime.of(2025, 1, 1, 10, 45, 0, 0, seoulZone)
            )

            // when
            val minutes = range.minutesBetween()

            // then
            assertThat(minutes).isEqualTo(45L)
        }

        @Test
        fun `should return zero when start equals end`() {
            // given
            val dateTime = ZonedDateTime.of(2025, 6, 15, 12, 30, 0, 0, seoulZone)
            val range = ZonedDateTimeRange.from(dateTime, dateTime)

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

    @Nested
    @DisplayName("timeOffset")
    inner class TimeOffsetTest {

        @Test
        fun `should return zero offset when same zone`() {
            // given
            val range = ZonedDateTimeRange.from(
                ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone),
                ZonedDateTime.of(2025, 1, 1, 18, 0, 0, 0, seoulZone)
            )

            // when
            val offset = range.timeOffset()

            // then
            assertThat(offset).isEqualTo(ZoneOffset.ofTotalSeconds(0))
        }

        @Test
        fun `should return correct offset for different zones`() {
            // given
            val start = ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, utcZone)
            val end = ZonedDateTime.of(2025, 1, 1, 19, 0, 0, 0, seoulZone)
            val range = ZonedDateTimeRange.from(start, end)

            // when
            val offset = range.timeOffset()

            // then
            assertThat(offset).isEqualTo(ZoneOffset.ofHours(9))
        }
    }

    @Nested
    @DisplayName("toLocalDateTimeRange")
    inner class ToLocalDateTimeRangeTest {

        @Test
        fun `should convert to LocalDateTimeRange`() {
            // given
            val start = ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, seoulZone)
            val end = ZonedDateTime.of(2025, 12, 31, 18, 0, 0, 0, seoulZone)
            val range = ZonedDateTimeRange.from(start, end)

            // when
            val localDateTimeRange = range.toLocalDateTimeRange()

            // then
            assertThat(localDateTimeRange.start).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0))
            assertThat(localDateTimeRange.end).isEqualTo(LocalDateTime.of(2025, 12, 31, 18, 0))
        }
    }
}
