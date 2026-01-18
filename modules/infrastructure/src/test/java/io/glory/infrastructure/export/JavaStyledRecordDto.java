package io.glory.infrastructure.export;

import io.glory.infrastructure.export.annotation.ExportAlignment;
import io.glory.infrastructure.export.annotation.ExportBorder;
import io.glory.infrastructure.export.annotation.ExportCellStyle;
import io.glory.infrastructure.export.annotation.ExportColor;
import io.glory.infrastructure.export.annotation.ExportColumn;
import io.glory.infrastructure.export.annotation.ExportSheet;

@ExportSheet(name = "스타일적용", includeIndex = false)
public record JavaStyledRecordDto(
    @ExportColumn(
        header = "상품명",
        order = 1,
        headerStyle = @ExportCellStyle(
            bold = true,
            bgColor = ExportColor.LIGHT_BLUE,
            alignment = ExportAlignment.CENTER
        ),
        bodyStyle = @ExportCellStyle(
            fontColor = ExportColor.BLUE
        )
    )
    String productName,

    @ExportColumn(
        header = "가격",
        order = 2,
        format = "#,##0",
        headerStyle = @ExportCellStyle(
            bold = true,
            bgColor = ExportColor.LIGHT_GREEN,
            alignment = ExportAlignment.CENTER,
            border = ExportBorder.THIN
        ),
        bodyStyle = @ExportCellStyle(
            alignment = ExportAlignment.RIGHT,
            border = ExportBorder.THIN
        )
    )
    int price,

    @ExportColumn(
        header = "상태",
        order = 3,
        headerStyle = @ExportCellStyle(
            bold = true,
            bgColor = ExportColor.LIGHT_YELLOW,
            alignment = ExportAlignment.CENTER
        )
    )
    String status
) {
}
