package io.glory.infrastructure.export.excel

import io.glory.infrastructure.export.ColumnMetaExtractor
import io.glory.infrastructure.export.JavaExportDto
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class JavaDtoExportTest {

    private lateinit var exporter: ExcelExporter

    @BeforeEach
    fun setUp(): Unit {
        exporter = ExcelExporter()
        ColumnMetaExtractor.clearCache()
    }

    @Test
    fun `should export Java DTO to excel`(): Unit {
        // given
        val data = listOf(
            JavaExportDto("홍길동", 30),
            JavaExportDto("김철수", 25),
        )
        val outputStream = ByteArrayOutputStream()

        // when
        exporter.export(data, JavaExportDto::class, outputStream)

        // then
        val workbook = XSSFWorkbook(ByteArrayInputStream(outputStream.toByteArray()))
        val sheet = workbook.getSheetAt(0)

        assertThat(sheet.sheetName).isEqualTo("자바DTO")
        assertThat(sheet.getRow(0).getCell(0).stringCellValue).isEqualTo("이름")
        assertThat(sheet.getRow(0).getCell(1).stringCellValue).isEqualTo("나이")
        assertThat(sheet.getRow(1).getCell(0).stringCellValue).isEqualTo("홍길동")
        assertThat(sheet.getRow(1).getCell(1).numericCellValue).isEqualTo(30.0)

        workbook.close()
    }

    @Test
    fun `should extract column metas from Java class`(): Unit {
        // when
        val metas = ColumnMetaExtractor.extractColumnMetas(JavaExportDto::class)

        // then
        println("Extracted metas: $metas")
        assertThat(metas).isNotEmpty
    }
}
