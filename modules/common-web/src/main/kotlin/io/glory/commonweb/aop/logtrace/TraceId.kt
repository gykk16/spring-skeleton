package io.glory.commonweb.aop.logtrace

import io.glory.common.TraceHeader.APP_TRACE_ID
import org.slf4j.MDC
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val FIRST_LEVEL = 1

class TraceId {

    @OptIn(ExperimentalUuidApi::class)
    val id: String = MDC.get(APP_TRACE_ID) ?: Uuid.generateV7().toString()

    var level: Int = FIRST_LEVEL
        private set

    val isFirstLevel: Boolean
        get() = level == FIRST_LEVEL

    fun nextLevel(): TraceId {
        level++
        return this
    }

    fun prevLevel(): TraceId {
        level--
        return this
    }
}
