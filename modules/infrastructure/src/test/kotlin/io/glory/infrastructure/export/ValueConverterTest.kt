package io.glory.infrastructure.export

import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ValueConverterTest {

    private lateinit var workbook: SXSSFWorkbook

    @BeforeEach
    fun setUp(): Unit {
        workbook = SXSSFWorkbook()
    }

    @AfterEach
    fun tearDown(): Unit {
        workbook.dispose()
        workbook.close()
    }

    @Test
    fun `should set string value`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val cell = sheet.createRow(0).createCell(0)

        // when
        ValueConverter.setCellValue(cell, "테스트", "", workbook)

        // then
        assertThat(cell.stringCellValue).isEqualTo("테스트")
    }

    @Test
    fun `should set boolean as Y or N`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val row = sheet.createRow(0)
        val cellTrue = row.createCell(0)
        val cellFalse = row.createCell(1)

        // when
        ValueConverter.setCellValue(cellTrue, true, "", workbook)
        ValueConverter.setCellValue(cellFalse, false, "", workbook)

        // then
        assertThat(cellTrue.stringCellValue).isEqualTo("Y")
        assertThat(cellFalse.stringCellValue).isEqualTo("N")
    }

    @Test
    fun `should set numeric values`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val row = sheet.createRow(0)
        val cellInt = row.createCell(0)
        val cellLong = row.createCell(1)
        val cellDouble = row.createCell(2)
        val cellBigDecimal = row.createCell(3)

        // when
        ValueConverter.setCellValue(cellInt, 100, "", workbook)
        ValueConverter.setCellValue(cellLong, 200L, "", workbook)
        ValueConverter.setCellValue(cellDouble, 300.5, "", workbook)
        ValueConverter.setCellValue(cellBigDecimal, BigDecimal("400.75"), "", workbook)

        // then
        assertThat(cellInt.numericCellValue).isEqualTo(100.0)
        assertThat(cellLong.numericCellValue).isEqualTo(200.0)
        assertThat(cellDouble.numericCellValue).isEqualTo(300.5)
        assertThat(cellBigDecimal.numericCellValue).isEqualTo(400.75)
    }

    @Test
    fun `should set LocalDate with default format`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val cell = sheet.createRow(0).createCell(0)
        val date = LocalDate.of(2025, 1, 17)

        // when
        ValueConverter.setCellValue(cell, date, "", workbook)

        // then
        assertThat(cell.stringCellValue).isEqualTo("2025-01-17")
    }

    @Test
    fun `should set LocalDate with custom format`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val cell = sheet.createRow(0).createCell(0)
        val date = LocalDate.of(2025, 1, 17)

        // when
        ValueConverter.setCellValue(cell, date, "yyyy/MM/dd", workbook)

        // then
        assertThat(cell.stringCellValue).isEqualTo("2025/01/17")
    }

    @Test
    fun `should set LocalDateTime with default format`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val cell = sheet.createRow(0).createCell(0)
        val dateTime = LocalDateTime.of(2025, 1, 17, 14, 30, 45)

        // when
        ValueConverter.setCellValue(cell, dateTime, "", workbook)

        // then
        assertThat(cell.stringCellValue).isEqualTo("2025-01-17 14:30:45")
    }

    @Test
    fun `should set LocalTime with default format`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val cell = sheet.createRow(0).createCell(0)
        val time = LocalTime.of(14, 30, 45)

        // when
        ValueConverter.setCellValue(cell, time, "", workbook)

        // then
        assertThat(cell.stringCellValue).isEqualTo("14:30:45")
    }

    @Test
    fun `should set enum as name`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val cell = sheet.createRow(0).createCell(0)

        // when
        ValueConverter.setCellValue(cell, TestStatus.ACTIVE, "", workbook)

        // then
        assertThat(cell.stringCellValue).isEqualTo("ACTIVE")
    }

    @Test
    fun `should set null as blank`(): Unit {
        // given
        val sheet = workbook.createSheet()
        val cell = sheet.createRow(0).createCell(0)

        // when
        ValueConverter.setCellValue(cell, null, "", workbook)

        // then
        assertThat(cell.cellType.name).isEqualTo("BLANK")
    }

    private enum class TestStatus {
        ACTIVE, INACTIVE
    }
}
