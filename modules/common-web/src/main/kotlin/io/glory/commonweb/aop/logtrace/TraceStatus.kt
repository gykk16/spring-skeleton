package io.glory.commonweb.aop.logtrace

/**
 * Trace status for logging
 * @see LogTrace
 */
@JvmRecord
data class TraceStatus(
    @JvmField val traceId: TraceId,
    @JvmField val startNanos: Long,
    @JvmField val message: String
)
