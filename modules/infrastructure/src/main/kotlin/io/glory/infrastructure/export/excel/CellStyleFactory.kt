package io.glory.infrastructure.export.excel

import io.glory.infrastructure.export.annotation.ExportAlignment
import io.glory.infrastructure.export.annotation.ExportBorder
import io.glory.infrastructure.export.annotation.ExportCellStyle
import io.glory.infrastructure.export.annotation.ExportColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.xssf.streaming.SXSSFWorkbook

/**
 * Excel CellStyle 팩토리
 *
 * - POI 64K CellStyle 제한 대응을 위한 스타일 캐싱
 * - 동일 스타일은 재사용하여 메모리 효율화
 */
class CellStyleFactory(val workbook: SXSSFWorkbook) {

    private val styleCache = mutableMapOf<String, CellStyle>()
    private val fontCache = mutableMapOf<String, Font>()
    private val dataFormat by lazy { workbook.createDataFormat() }

    /**
     * ExportCellStyle 어노테이션 기반 CellStyle 생성/조회
     *
     * @param style 스타일 어노테이션
     * @param format 셀 포맷 (숫자/날짜)
     * @return 캐싱된 또는 새로 생성된 CellStyle
     */
    fun getStyle(style: ExportCellStyle, format: String? = null): CellStyle {
        val key = buildStyleKey(style, format)
        return styleCache.getOrPut(key) {
            createCellStyle(style, format)
        }
    }

    /**
     * 기본 헤더 스타일 생성
     *
     * - 굵은 글씨
     * - 회색 배경
     * - 가운데 정렬
     */
    fun getDefaultHeaderStyle(): CellStyle {
        return getStyle(DEFAULT_HEADER_STYLE)
    }

    /**
     * 커스텀 헤더 스타일이 기본 스타일인지 확인
     */
    fun isDefaultStyle(style: ExportCellStyle): Boolean {
        return !style.bold &&
            !style.italic &&
            style.fontSize == (-1).toShort() &&
            style.fontColor == ExportColor.BLACK &&
            style.bgColor == ExportColor.NONE &&
            style.alignment == ExportAlignment.LEFT &&
            style.border == ExportBorder.NONE
    }

    private fun createCellStyle(style: ExportCellStyle, format: String?): CellStyle {
        return workbook.createCellStyle().apply {
            // 배경색
            if (style.bgColor != ExportColor.NONE) {
                fillForegroundColor = style.bgColor.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
            }

            // 폰트 스타일
            if (needsFont(style)) {
                setFont(getFont(style))
            }

            // 정렬
            alignment = style.alignment.poiAlignment

            // 테두리
            if (style.border != ExportBorder.NONE) {
                borderTop = style.border.poiBorderStyle
                borderBottom = style.border.poiBorderStyle
                borderLeft = style.border.poiBorderStyle
                borderRight = style.border.poiBorderStyle

                if (style.borderColor != ExportColor.BLACK) {
                    topBorderColor = style.borderColor.index
                    bottomBorderColor = style.borderColor.index
                    leftBorderColor = style.borderColor.index
                    rightBorderColor = style.borderColor.index
                }
            }

            // 포맷
            if (!format.isNullOrBlank()) {
                dataFormat = this@CellStyleFactory.dataFormat.getFormat(format)
            }
        }
    }

    private fun needsFont(style: ExportCellStyle): Boolean {
        return style.bold ||
            style.italic ||
            style.fontSize > 0 ||
            style.fontColor != ExportColor.BLACK
    }

    private fun getFont(style: ExportCellStyle): Font {
        val key = buildFontKey(style)
        return fontCache.getOrPut(key) {
            workbook.createFont().apply {
                bold = style.bold
                italic = style.italic
                if (style.fontSize > 0) {
                    fontHeightInPoints = style.fontSize
                }
                if (style.fontColor != ExportColor.BLACK) {
                    color = style.fontColor.index
                }
            }
        }
    }

    private fun buildStyleKey(style: ExportCellStyle, format: String?): String {
        return "style_${style.bgColor}_${style.fontColor}_${style.bold}_${style.italic}_${style.fontSize}_${style.alignment}_${style.border}_${style.borderColor}_$format"
    }

    private fun buildFontKey(style: ExportCellStyle): String {
        return "font_${style.bold}_${style.italic}_${style.fontSize}_${style.fontColor}"
    }

    companion object {
        private val DEFAULT_HEADER_STYLE = ExportCellStyle(
            bold = true,
            bgColor = ExportColor.GREY_25,
            alignment = ExportAlignment.CENTER,
        )
    }
}
