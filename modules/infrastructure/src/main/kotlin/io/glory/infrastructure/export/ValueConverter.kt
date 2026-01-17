package io.glory.infrastructure.export

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * 타입별 셀 값 변환기
 */
object ValueConverter {

    private val defaultDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val defaultDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val defaultTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

    /**
     * 값을 셀에 설정
     *
     * @param cell 대상 셀
     * @param value 설정할 값
     * @param format 포맷 문자열 (날짜/숫자용)
     * @param workbook Workbook (DataFormat 생성용)
     * @param baseStyle 기본 스타일 (포맷 적용 시 복사)
     */
    fun setCellValue(
        cell: Cell,
        value: Any?,
        format: String,
        workbook: Workbook,
        baseStyle: CellStyle? = null,
    ) {
        when (value) {
            null -> cell.setBlank()
            is String -> cell.setCellValue(value)
            is Boolean -> cell.setCellValue(if (value) "Y" else "N")
            is Int -> setNumericValue(cell, value.toDouble(), format, workbook, baseStyle)
            is Long -> setNumericValue(cell, value.toDouble(), format, workbook, baseStyle)
            is Double -> setNumericValue(cell, value, format, workbook, baseStyle)
            is Float -> setNumericValue(cell, value.toDouble(), format, workbook, baseStyle)
            is BigDecimal -> setNumericValue(cell, value.toDouble(), format, workbook, baseStyle)
            is LocalDate -> setDateValue(cell, value, format)
            is LocalDateTime -> setDateTimeValue(cell, value, format)
            is LocalTime -> setTimeValue(cell, value, format)
            is ZonedDateTime -> setDateTimeValue(cell, value.toLocalDateTime(), format)
            is Enum<*> -> cell.setCellValue(value.name)
            else -> cell.setCellValue(value.toString())
        }
    }

    private fun setNumericValue(
        cell: Cell,
        value: Double,
        format: String,
        workbook: Workbook,
        baseStyle: CellStyle?,
    ) {
        cell.setCellValue(value)
        if (format.isNotBlank()) {
            val style = baseStyle?.let { workbook.createCellStyle().apply { cloneStyleFrom(it) } }
                ?: workbook.createCellStyle()
            style.dataFormat = workbook.createDataFormat().getFormat(format)
            cell.cellStyle = style
        }
    }

    private fun setDateValue(cell: Cell, value: LocalDate, format: String) {
        val formatter = if (format.isNotBlank()) {
            DateTimeFormatter.ofPattern(format)
        } else {
            defaultDateFormat
        }
        cell.setCellValue(value.format(formatter))
    }

    private fun setDateTimeValue(cell: Cell, value: LocalDateTime, format: String) {
        val formatter = if (format.isNotBlank()) {
            DateTimeFormatter.ofPattern(format)
        } else {
            defaultDateTimeFormat
        }
        cell.setCellValue(value.format(formatter))
    }

    private fun setTimeValue(cell: Cell, value: LocalTime, format: String) {
        val formatter = if (format.isNotBlank()) {
            DateTimeFormatter.ofPattern(format)
        } else {
            defaultTimeFormat
        }
        cell.setCellValue(value.format(formatter))
    }

    private fun Cell.setBlank() {
        setCellType(CellType.BLANK)
    }
}
