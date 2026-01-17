package io.glory.infrastructure.export;

import io.glory.infrastructure.export.annotation.ExportColumn;
import io.glory.infrastructure.export.annotation.ExportSheet;

@ExportSheet(name = "자바레코드")
public record JavaRecordDto(
    @ExportColumn(header = "상품명", order = 1)
    String productName,

    @ExportColumn(header = "가격", order = 2, format = "#,##0")
    int price,

    @ExportColumn(header = "재고", order = 3)
    int stock,

    @ExportColumn(header = "판매중", order = 4)
    boolean onSale
) {
}
