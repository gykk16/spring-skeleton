package io.glory.commonapiapp.docs

import io.glory.commonapiapp.api.HolidayController
import io.glory.commonapplication.service.HolidayService
import io.glory.domain.holiday.Holiday
import io.glory.testsupport.restdocs.DocsFieldType.ARRAY
import io.glory.testsupport.restdocs.DocsFieldType.DATE
import io.glory.testsupport.restdocs.DocsFieldType.NUMBER
import io.glory.testsupport.restdocs.DocsFieldType.OBJECT
import io.glory.testsupport.restdocs.DocsFieldType.STRING
import io.glory.testsupport.restdocs.RestDocsSupport
import io.glory.testsupport.restdocs.RestDocsSupport.Companion.dataResponseFields
import io.glory.testsupport.restdocs.RestDocsSupport.Companion.responseArrayCommonFieldsSubsection
import io.glory.testsupport.restdocs.RestDocsSupport.Companion.responseCommonFields
import io.glory.testsupport.restdocs.RestDocsSupport.Companion.responseCommonFieldsSubsection
import io.glory.testsupport.restdocs.RestDocsSupport.Companion.responseStringCommonFields
import io.glory.testsupport.restdocs.fields
import io.glory.testsupport.restdocs.type
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

class HolidayControllerDocsTest : RestDocsSupport() {

    private val holidayService: HolidayService = mock()

    override fun initController(): Any = HolidayController(holidayService)

    @Test
    fun `get holidays by year`(): Unit {
        // given
        val holidays = listOf(
            Holiday(1L, LocalDate.of(2026, 2, 16), "설날"),
            Holiday(2L, LocalDate.of(2026, 2, 17), "설날"),
            Holiday(3L, LocalDate.of(2026, 3, 1), "삼일절"),
        )
        given(holidayService.findByYear(2026)).willReturn(holidays)

        // when & then
        mockMvc.perform(get("/api/holidays/{year}", 2026))
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("year").description("조회할 연도"),
                    ),
                    responseFields(*responseCommonFieldsSubsection()),
                    dataResponseFields(
                        "holidays" type ARRAY means "공휴일 목록",
                        "holidays[].date" type DATE means "공휴일 날짜",
                        "holidays[].name" type STRING means "공휴일 이름",
                    ),
                )
            )
    }

    @Test
    fun `get holidays by year and month`(): Unit {
        // given
        val holidays = listOf(
            Holiday(1L, LocalDate.of(2026, 2, 16), "설날"),
            Holiday(2L, LocalDate.of(2026, 2, 17), "설날"),
        )
        given(holidayService.findByYearAndMonth(2026, 2)).willReturn(holidays)

        // when & then
        mockMvc.perform(get("/api/holidays/{year}/{month}", 2026, 2))
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("year").description("조회할 연도"),
                        parameterWithName("month").description("조회할 월"),
                    ),
                    responseFields(*responseCommonFieldsSubsection()),
                    dataResponseFields(
                        "holidays" type ARRAY means "공휴일 목록",
                        "holidays[].date" type DATE means "공휴일 날짜",
                        "holidays[].name" type STRING means "공휴일 이름",
                    ),
                )
            )
    }

    @Test
    fun `get holidays by date`(): Unit {
        // given
        val holidays = listOf(
            Holiday(1L, LocalDate.of(2026, 2, 16), "설날"),
        )
        given(holidayService.findByDate(2026, 2, 16)).willReturn(holidays)

        // when & then
        mockMvc.perform(get("/api/holidays/{year}/{month}/{day}", 2026, 2, 16))
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("year").description("조회할 연도"),
                        parameterWithName("month").description("조회할 월"),
                        parameterWithName("day").description("조회할 일"),
                    ),
                    responseFields(*responseCommonFieldsSubsection()),
                    dataResponseFields(
                        "holidays" type ARRAY means "공휴일 목록",
                        "holidays[].date" type DATE means "공휴일 날짜",
                        "holidays[].name" type STRING means "공휴일 이름",
                    ),
                )
            )
    }

    @Test
    fun `create holiday`(): Unit {
        // given
        val holiday = Holiday(1L, LocalDate.of(2026, 1, 1), "신정")
        given(holidayService.create(any())).willReturn(holiday)

        val request = """
            {
                "holidayDate": "2026-01-01",
                "name": "신정"
            }
        """.trimIndent()

        // when & then
        mockMvc.perform(
            post("/api/holidays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        )
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    requestFields(
                        *fields(
                            "holidayDate" type DATE means "공휴일 날짜",
                            "name" type STRING means "공휴일 이름",
                        )
                    ),
                    responseFields(*responseCommonFieldsSubsection()),
                    dataResponseFields(
                        "id" type NUMBER means "공휴일 ID",
                        "holidayDate" type DATE means "공휴일 날짜",
                        "name" type STRING means "공휴일 이름",
                    ),
                )
            )
    }

    @Test
    fun `create holidays bulk`(): Unit {
        // given
        val holidays = listOf(
            Holiday(1L, LocalDate.of(2026, 2, 16), "설날"),
            Holiday(2L, LocalDate.of(2026, 2, 17), "설날"),
        )
        given(holidayService.createAll(any())).willReturn(holidays)

        val request = """
            {
                "holidays": [
                    { "holidayDate": "2026-02-16", "name": "설날" },
                    { "holidayDate": "2026-02-17", "name": "설날" }
                ]
            }
        """.trimIndent()

        // when & then
        mockMvc.perform(
            post("/api/holidays/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        )
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    requestFields(
                        *fields(
                            "holidays" type ARRAY means "생성할 공휴일 목록",
                            "holidays[].holidayDate" type DATE means "공휴일 날짜",
                            "holidays[].name" type STRING means "공휴일 이름",
                        )
                    ),
                    responseFields(*responseArrayCommonFieldsSubsection()),
                    dataResponseFields(
                        "id" type NUMBER means "공휴일 ID",
                        "holidayDate" type DATE means "공휴일 날짜",
                        "name" type STRING means "공휴일 이름",
                    ),
                )
            )
    }

    @Test
    fun `update holiday`(): Unit {
        // given
        val holiday = Holiday(1L, LocalDate.of(2026, 2, 16), "설날 (수정)")
        given(holidayService.update(any(), any())).willReturn(holiday)

        val request = """
            {
                "holidayDate": "2026-02-16",
                "name": "설날 (수정)"
            }
        """.trimIndent()

        // when & then
        mockMvc.perform(
            put("/api/holidays/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
        )
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("id").description("수정할 공휴일 ID"),
                    ),
                    requestFields(
                        *fields(
                            "holidayDate" type DATE means "공휴일 날짜",
                            "name" type STRING means "공휴일 이름",
                        )
                    ),
                    responseFields(*responseCommonFieldsSubsection()),
                    dataResponseFields(
                        "id" type NUMBER means "공휴일 ID",
                        "holidayDate" type DATE means "공휴일 날짜",
                        "name" type STRING means "공휴일 이름",
                    ),
                )
            )
    }

    @Test
    fun `delete holiday`(): Unit {
        // given
        doNothing().`when`(holidayService).delete(any())

        // when & then
        mockMvc.perform(delete("/api/holidays/{id}", 1L))
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("id").description("삭제할 공휴일 ID"),
                    ),
                    responseFields(*responseStringCommonFields()),
                )
            )
    }
}
