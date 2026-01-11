package io.glory.common.utils

import io.glory.common.codes.response.ErrorCode
import io.glory.common.exceptions.KnownException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun knownRequired(value: Boolean, lazyMessage: () -> Any) {
    contract {
        returns() implies value
    }
    if (!value) {
        val message = lazyMessage()
        throw KnownException(ErrorCode.ILLEGAL_ARGUMENT, message.toString())
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T : Any> knownRequiredNotNull(value: T?, lazyMessage: () -> Any): T {
    contract {
        returns() implies (value != null)
    }
    if (value == null) {
        val message = lazyMessage()
        throw KnownException(ErrorCode.ILLEGAL_ARGUMENT, message.toString())
    }
    return value
}

@OptIn(ExperimentalContracts::class)
inline fun <Any> knownNotBlank(value: String?, lazyMessage: () -> Any): String {
    contract {
        returns() implies (value != null)
    }
    if (value.isNullOrBlank()) {
        val message = lazyMessage()
        throw KnownException(ErrorCode.ILLEGAL_ARGUMENT, message.toString())
    }
    return value
}