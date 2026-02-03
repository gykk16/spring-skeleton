package io.glory.testsupport.restdocs.common

import io.glory.common.codes.ResponseCode
import io.glory.common.codes.response.ErrorCode
import io.glory.common.codes.response.SuccessCode
import io.glory.commonweb.response.resource.ApiResource
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Test-only controller for documenting common response formats and response codes.
 */
@RestController
class CommonResponseController {

    @GetMapping("/docs/response")
    fun response(): ResponseEntity<ApiResource<String>> =
        ApiResource.success()

    @GetMapping("/docs/response/page")
    fun responsePage(): ResponseEntity<ApiResource<List<Any>>> =
        ApiResource.ofPage(Page.empty())

    @GetMapping("/docs/response/no-offset-page")
    fun responseNoOffsetPage(): ResponseEntity<ApiResource<List<Any>>> =
        ApiResource.ofNoOffsetPage(Page.empty(), "")

    @GetMapping("/docs/response-codes")
    fun responseCodes(): ResponseEntity<ApiResource<ResponseCodeDocs>> {
        val successCodes = SuccessCode.entries.toCodeMap()
        val errorCodes = ErrorCode.entries.toCodeMap()
        return ApiResource.success(ResponseCodeDocs(successCodes, errorCodes))
    }

    private fun List<ResponseCode>.toCodeMap(): Map<String, List<String>> =
        associate { it.name to listOf(it.status.toString(), it.message) }
}

data class ResponseCodeDocs(
    val successCode: Map<String, List<String>>,
    val errorCode: Map<String, List<String>>,
)
