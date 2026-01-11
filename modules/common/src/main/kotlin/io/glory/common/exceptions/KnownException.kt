package io.glory.common.exceptions

import io.glory.common.codes.ResponseCode

/**
 * GlobalExceptionHandler 이 로깅 하지 않는 exception
 */
open class KnownException @JvmOverloads constructor(
    override val code: ResponseCode,
    override val message: String = code.message,
    cause: Throwable? = null,
) : BizRuntimeException(
    code = code,
    message = message,
    cause = cause,
    printStackTrace = false
)
