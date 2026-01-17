package io.glory.infrastructure.export.excel

import io.glory.infrastructure.export.ColumnMeta
import io.glory.infrastructure.export.ColumnMetaExtractor
import io.glory.infrastructure.export.DataExporter
import io.glory.infrastructure.export.SheetMeta
import io.glory.infrastructure.export.ValueConverter
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Component
import java.io.OutputStream
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * SXSSF 기반 Excel Exporter
 *
 * - Streaming 방식으로 대용량 데이터 처리
 * - 메모리에 지정된 행 수만 유지 (기본 500행)
 */
@Component
class ExcelExporter(
    private val rowAccessWindowSize: Int = DEFAULT_ROW_ACCESS_WINDOW_SIZE,
) : DataExporter {

    override fun <T : Any> export(
        data: List<T>,
        clazz: KClass<T>,
        outputStream: OutputStream,
    ) {
        createWorkbook(clazz, outputStream) { sheet, columnMetas, sheetMeta, workbook, headerStyle ->
            var rowIndex = 1
            data.forEach { item ->
                createDataRow(sheet, rowIndex++, item, columnMetas, sheetMeta, workbook)
            }
        }
    }

    override fun <T : Any> exportWithChunks(
        clazz: KClass<T>,
        outputStream: OutputStream,
        chunkFetcher: (consumer: (List<T>) -> Unit) -> Unit,
    ) {
        createWorkbook(clazz, outputStream) { sheet, columnMetas, sheetMeta, workbook, headerStyle ->
            var rowIndex = 1
            chunkFetcher { chunk ->
                chunk.forEach { item ->
                    createDataRow(sheet, rowIndex++, item, columnMetas, sheetMeta, workbook)
                }
            }
        }
    }

    private fun <T : Any> createWorkbook(
        clazz: KClass<T>,
        outputStream: OutputStream,
        dataWriter: (SXSSFSheet, List<ColumnMeta>, SheetMeta, SXSSFWorkbook, CellStyle) -> Unit,
    ) {
        val workbook = SXSSFWorkbook(rowAccessWindowSize)
        try {
            val sheetMeta = ColumnMetaExtractor.extractSheetMeta(clazz)
            val columnMetas = ColumnMetaExtractor.extractColumnMetas(clazz)
            val headerStyle = createHeaderStyle(workbook)

            val sheet = workbook.createSheet(sheetMeta.name)
            createHeaderRow(sheet, columnMetas, sheetMeta, headerStyle)

            if (sheetMeta.freezeHeader) {
                sheet.createFreezePane(0, 1)
            }

            dataWriter(sheet, columnMetas, sheetMeta, workbook, headerStyle)

            setColumnWidths(sheet, columnMetas, sheetMeta)

            workbook.write(outputStream)
        } finally {
            workbook.dispose()
            workbook.close()
        }
    }

    private fun createHeaderRow(
        sheet: SXSSFSheet,
        columnMetas: List<ColumnMeta>,
        sheetMeta: SheetMeta,
        headerStyle: CellStyle,
    ) {
        val row = sheet.createRow(0)
        var colIndex = 0

        if (sheetMeta.includeIndex) {
            val cell = row.createCell(colIndex++)
            cell.setCellValue(sheetMeta.indexHeader)
            cell.cellStyle = headerStyle
        }

        columnMetas.forEach { meta ->
            val cell = row.createCell(colIndex++)
            cell.setCellValue(meta.header)
            cell.cellStyle = headerStyle
        }
    }

    private fun <T : Any> createDataRow(
        sheet: SXSSFSheet,
        rowIndex: Int,
        item: T,
        columnMetas: List<ColumnMeta>,
        sheetMeta: SheetMeta,
        workbook: SXSSFWorkbook,
    ) {
        val row = sheet.createRow(rowIndex)
        var colIndex = 0

        if (sheetMeta.includeIndex) {
            val cell = row.createCell(colIndex++)
            cell.setCellValue(rowIndex.toDouble())
        }

        columnMetas.forEach { meta ->
            val cell = row.createCell(colIndex++)
            @Suppress("UNCHECKED_CAST")
            val property = meta.property as KProperty1<T, *>
            val value = property.get(item)
            ValueConverter.setCellValue(cell, value, meta.format, workbook)
        }
    }

    private fun setColumnWidths(
        sheet: SXSSFSheet,
        columnMetas: List<ColumnMeta>,
        sheetMeta: SheetMeta,
    ) {
        var colIndex = 0

        if (sheetMeta.includeIndex) {
            sheet.setColumnWidth(colIndex++, sheetMeta.indexWidth * COLUMN_WIDTH_UNIT)
        }

        columnMetas.forEach { meta ->
            if (meta.width > 0) {
                sheet.setColumnWidth(colIndex, meta.width * COLUMN_WIDTH_UNIT)
            }
            colIndex++
        }
    }

    private fun createHeaderStyle(workbook: SXSSFWorkbook): CellStyle {
        return workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN

            val font = workbook.createFont().apply {
                bold = true
            }
            setFont(font)
        }
    }

    companion object {
        private const val DEFAULT_ROW_ACCESS_WINDOW_SIZE = 500
        private const val COLUMN_WIDTH_UNIT = 256
    }
}
