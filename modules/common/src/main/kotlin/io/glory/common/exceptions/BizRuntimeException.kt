package io.glory.common.exceptions

import io.glory.common.codes.ResponseCode

open class BizRuntimeException @JvmOverloads constructor(
    open val code: ResponseCode,
    override val message: String = code.message,
    override val cause: Throwable? = null,
    val printStackTrace: Boolean = false
) : RuntimeException(message, cause) {

    fun simplePrint(): String {
        return "[$code] $message"
    }

}