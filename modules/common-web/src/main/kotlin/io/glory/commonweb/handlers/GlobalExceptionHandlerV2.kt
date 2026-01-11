package io.glory.commonweb.handlers

import io.github.oshai.kotlinlogging.KotlinLogging
import io.glory.common.codes.ResponseCode
import io.glory.common.codes.response.ErrorCode
import io.glory.common.exceptions.BizException
import io.glory.common.exceptions.BizRuntimeException
import io.glory.common.exceptions.KnownException
import io.glory.commonweb.response.resource.ApiResource
import io.glory.commonweb.utils.ErrorLogPrintUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * Global exception handler V2
 *
 * @see BizRuntimeException
 * @see BizException
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class GlobalExceptionHandlerV2 {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(KnownException::class)
    fun knownException(
        request: HttpServletRequest, e: KnownException,
    ): ResponseEntity<ApiResource<Any>> {
        return createApiResource(request, e.code, e, log = false)
    }

//    @ExceptionHandler(JwtAuthenticationException::class)
//    fun jwtAuthenticationException(
//        request: HttpServletRequest, e: JwtAuthenticationException,
//    ): ResponseEntity<ApiResource<Any>> {
//        return createApiResource(request, e.code, e)
//    }

    @ExceptionHandler(BizRuntimeException::class)
    fun bizRuntimeException(
        request: HttpServletRequest, e: BizRuntimeException,
    ): ResponseEntity<ApiResource<Any>> {
        return createApiResource(request, e.code, e, e.printStackTrace)
    }

    @ExceptionHandler(BizException::class)
    fun bizException(
        request: HttpServletRequest, e: BizRuntimeException,
    ): ResponseEntity<ApiResource<Any>> {
        return createApiResource(request, e.code, e, e.printStackTrace)
    }

    @ExceptionHandler(
        NoResourceFoundException::class,
        HttpRequestMethodNotSupportedException::class,
        MethodArgumentNotValidException::class,
        MissingRequestValueException::class,
        MethodArgumentTypeMismatchException::class,
        MissingServletRequestPartException::class,
        HandlerMethodValidationException::class,
        HttpMessageNotReadableException::class,
        MultipartException::class,
        AccessDeniedException::class,
//        AuthenticationException::class,
//        JwtException::class,
        Exception::class
    )
    fun handleException(request: HttpServletRequest, ex: Exception): ResponseEntity<ApiResource<Any>> {
        return when (ex) {
            is NoResourceFoundException -> {
                handleNoResourceFoundException(request, ex, ErrorCode.NOT_FOUND)
            }

            is HttpRequestMethodNotSupportedException -> {
                handleHttpRequestMethodNotSupportedException(request, ex, ErrorCode.NOT_FOUND)
            }

            is MethodArgumentNotValidException -> {
                handleMethodArgumentNotValidException(request, ex, ErrorCode.INVALID_ARGUMENT)
            }

            is MethodArgumentTypeMismatchException -> {
                handleMethodArgumentTypeMismatchException(request, ex, ErrorCode.INVALID_ARGUMENT)
            }

            is MissingRequestValueException -> {
                handleMissingRequestValueException(request, ex, ErrorCode.INVALID_ARGUMENT)
            }

            is MissingServletRequestPartException -> {
                handleMissingServletRequestPartException(request, ex, ErrorCode.INVALID_ARGUMENT)
            }

            is HandlerMethodValidationException -> {
                handleHandlerMethodValidationException(request, ex, ErrorCode.INVALID_ARGUMENT)
            }

            is HttpMessageNotReadableException,
            is HttpMediaTypeNotSupportedException,
                -> {
                handleHttpMessageNotReadableException(request, ex, ErrorCode.NOT_READABLE)
            }

            is MultipartException -> {
                handleMultipartException(request, ex, ErrorCode.INVALID_ARGUMENT)
            }

            is AccessDeniedException -> {
                handleAccessDeniedException(request, ex, ErrorCode.FORBIDDEN)
            }

//            is AuthenticationException -> {
//                handleAuthenticationException(request, ex, ErrorCode.UNAUTHORIZED)
//            }
//
//            is JwtException -> {
//                handleJwtException(request, ex, TokenErrorCode.TOKEN_ERROR)
//            }

            is IllegalArgumentException -> {
                createApiResource(request, ErrorCode.ILLEGAL_ARGUMENT, ex)
            }

            is IllegalStateException -> {
                createApiResource(request, ErrorCode.ILLEGAL_STATE, ex)
            }

            is NoSuchElementException -> {
                createApiResource(request, ErrorCode.DATA_NOT_FOUND, ex)
            }

            is UnsupportedOperationException -> {
                createApiResource(request, ErrorCode.UNSUPPORTED_OPERATION, ex)
            }

            else -> {
                createApiResource(request, ErrorCode.SERVER_ERROR, ex, data = ErrorCode.SERVER_ERROR.message)
            }
        }
    }

    private fun handleNoResourceFoundException(
        request: HttpServletRequest, ex: NoResourceFoundException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        return createApiResource(request, errorCode, ex)
    }

    private fun handleHttpRequestMethodNotSupportedException(
        request: HttpServletRequest, ex: HttpRequestMethodNotSupportedException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        return createApiResource(request, errorCode, ex)
    }

    private fun handleMethodArgumentNotValidException(
        request: HttpServletRequest, ex: MethodArgumentNotValidException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        val errors = mutableMapOf<String, String?>()
        ex.bindingResult.allErrors.forEach { error ->
            if (error is FieldError) {
                val fieldName = error.field
                val errorMessage = error.defaultMessage
                errors[fieldName] = errorMessage
            }
        }

        return createApiResource(request, errorCode, ex, false, data = errors)
    }

    private fun handleMethodArgumentTypeMismatchException(
        request: HttpServletRequest, ex: MethodArgumentTypeMismatchException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        val detail = "Type mismatch: '" + ex.value + "' is not acceptable in property '" + ex.propertyName + "'"
        return createApiResource(request, errorCode, ex, false, data = detail)
    }

    private fun handleMissingRequestValueException(
        request: HttpServletRequest, ex: MissingRequestValueException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        val detail = ex.body.detail ?: errorCode.message
        return createApiResource(request, errorCode, ex, false, data = detail)
    }

    private fun handleMissingServletRequestPartException(
        request: HttpServletRequest, ex: MissingServletRequestPartException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        val detail = ex.body.detail ?: errorCode.message
        return createApiResource(request, errorCode, ex, data = detail)
    }

    private fun handleHandlerMethodValidationException(
        request: HttpServletRequest, ex: HandlerMethodValidationException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        val errors = mutableMapOf<String, String?>()
        ex.parameterValidationResults
            .flatMap { it.resolvableErrors }
            .forEach { fieldError ->
                val fieldName = fieldError.codes?.lastOrNull().orEmpty()
                val errorMessage = fieldError.defaultMessage
                errors[fieldName] = errorMessage
            }

        return createApiResource(request, errorCode, ex, false, data = errors)
    }

    private fun handleHttpMessageNotReadableException(
        request: HttpServletRequest, ex: Exception, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        val message = ex.message?.split(":")?.get(0) ?: errorCode.message
        return createApiResource(request, errorCode, ex, false, data = message)
    }

    private fun handleMultipartException(
        request: HttpServletRequest, ex: MultipartException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        return createApiResource(request, errorCode, ex)
    }

    private fun handleAccessDeniedException(
        request: HttpServletRequest, ex: AccessDeniedException, errorCode: ResponseCode,
    ): ResponseEntity<ApiResource<Any>> {
        return createApiResource(request, errorCode, ex)
    }

//    private fun handleAuthenticationException(
//        request: HttpServletRequest, ex: AuthenticationException, errorCode: ResponseCode,
//    ): ResponseEntity<ApiResource<Any>> {
//        return createApiResource(request, errorCode, ex)
//    }
//
//    private fun handleJwtException(
//        request: HttpServletRequest, ex: JwtException, errorCode: ResponseCode,
//    ): ResponseEntity<ApiResource<Any>> {
//        return createApiResource(request, errorCode, ex)
//    }

    companion object {

        @JvmOverloads
        fun createApiResource(
            request: HttpServletRequest,
            code: ResponseCode,
            e: Exception,
            printStackTrace: Boolean = true,
            log: Boolean = true,
            data: Any = e.message ?: code.message,
        ): ResponseEntity<ApiResource<Any>> {
            if (log) {
                ErrorLogPrintUtil.logError(request, code, e, printStackTrace)
            }
            return ApiResource.of(code = code, data = data)
        }

    }

    init {
        logger.info { "# ==> ${this.javaClass.simpleName} initialized" }
    }

}