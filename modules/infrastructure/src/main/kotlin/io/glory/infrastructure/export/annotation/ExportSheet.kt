package io.glory.infrastructure.export.annotation

/**
 * 엑셀 시트 레벨 설정
 *
 * @property name 시트명
 * @property freezeHeader 헤더 행 고정 여부
 * @property includeIndex 인덱스 컬럼 포함 여부 (No.)
 * @property indexHeader 인덱스 헤더명
 * @property indexWidth 인덱스 컬럼 너비
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExportSheet(
    val name: String = "Sheet1",
    val freezeHeader: Boolean = true,
    val includeIndex: Boolean = false,
    val indexHeader: String = "No.",
    val indexWidth: Int = 6,
)
