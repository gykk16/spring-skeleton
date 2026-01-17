package io.glory.infrastructure.export.excel

import io.glory.infrastructure.export.ColumnMeta
import io.glory.infrastructure.export.ColumnMetaExtractor
import io.glory.infrastructure.export.DataExporter
import io.glory.infrastructure.export.SheetMeta
import io.glory.infrastructure.export.ValueConverter
import org.apache.poi.ss.usermodel.CellStyle
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
 * - CellStyleFactory를 통한 스타일 캐싱 (POI 64K 제한 대응)
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
        createWorkbook(clazz, outputStream) { sheet, columnMetas, sheetMeta, styleFactory ->
            var rowIndex = 1
            data.forEach { item ->
                createDataRow(sheet, rowIndex++, item, columnMetas, sheetMeta, styleFactory)
            }
        }
    }

    override fun <T : Any> exportWithChunks(
        clazz: KClass<T>,
        outputStream: OutputStream,
        chunkFetcher: (consumer: (List<T>) -> Unit) -> Unit,
    ) {
        createWorkbook(clazz, outputStream) { sheet, columnMetas, sheetMeta, styleFactory ->
            var rowIndex = 1
            chunkFetcher { chunk ->
                chunk.forEach { item ->
                    createDataRow(sheet, rowIndex++, item, columnMetas, sheetMeta, styleFactory)
                }
            }
        }
    }

    private fun <T : Any> createWorkbook(
        clazz: KClass<T>,
        outputStream: OutputStream,
        dataWriter: (SXSSFSheet, List<ColumnMeta>, SheetMeta, CellStyleFactory) -> Unit,
    ) {
        val workbook = SXSSFWorkbook(rowAccessWindowSize)
        try {
            val sheetMeta = ColumnMetaExtractor.extractSheetMeta(clazz)
            val columnMetas = ColumnMetaExtractor.extractColumnMetas(clazz)
            val styleFactory = CellStyleFactory(workbook)

            val sheet = workbook.createSheet(sheetMeta.name)
            createHeaderRow(sheet, columnMetas, sheetMeta, styleFactory)

            if (sheetMeta.freezeHeader) {
                sheet.createFreezePane(0, 1)
            }

            dataWriter(sheet, columnMetas, sheetMeta, styleFactory)

            setColumnWidths(sheet, columnMetas, sheetMeta)

            workbook.write(outputStream)
        } finally {
            workbook.close()
        }
    }

    private fun createHeaderRow(
        sheet: SXSSFSheet,
        columnMetas: List<ColumnMeta>,
        sheetMeta: SheetMeta,
        styleFactory: CellStyleFactory,
    ) {
        val row = sheet.createRow(0)
        var colIndex = 0
        val defaultHeaderStyle = styleFactory.getDefaultHeaderStyle()

        if (sheetMeta.includeIndex) {
            val cell = row.createCell(colIndex++)
            cell.setCellValue(sheetMeta.indexHeader)
            cell.cellStyle = defaultHeaderStyle
        }

        columnMetas.forEach { meta ->
            val cell = row.createCell(colIndex++)
            cell.setCellValue(meta.header)

            // 커스텀 헤더 스타일이 있으면 사용, 없으면 기본 스타일
            cell.cellStyle = if (styleFactory.isDefaultStyle(meta.headerStyle)) {
                defaultHeaderStyle
            } else {
                styleFactory.getStyle(meta.headerStyle)
            }
        }
    }

    private fun <T : Any> createDataRow(
        sheet: SXSSFSheet,
        rowIndex: Int,
        item: T,
        columnMetas: List<ColumnMeta>,
        sheetMeta: SheetMeta,
        styleFactory: CellStyleFactory,
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

            val bodyStyle = getBodyStyle(meta, styleFactory)
            ValueConverter.setCellValue(cell, value, meta.format, bodyStyle)
        }
    }

    private fun getBodyStyle(meta: ColumnMeta, styleFactory: CellStyleFactory): CellStyle? {
        val hasCustomStyle = !styleFactory.isDefaultStyle(meta.bodyStyle)
        val hasFormat = meta.format.isNotBlank()

        return if (hasCustomStyle || hasFormat) {
            styleFactory.getStyle(meta.bodyStyle, meta.format.ifBlank { null })
        } else {
            null
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

    companion object {
        private const val DEFAULT_ROW_ACCESS_WINDOW_SIZE = 500
        private const val COLUMN_WIDTH_UNIT = 256
    }
}
