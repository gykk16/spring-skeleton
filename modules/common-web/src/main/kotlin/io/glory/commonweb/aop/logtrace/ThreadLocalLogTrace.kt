package io.glory.commonweb.aop.logtrace

import io.github.oshai.kotlinlogging.KotlinLogging
import io.glory.common.exceptions.KnownException

private val logger = KotlinLogging.logger {}

private const val START_PREFIX = "--> "
private const val END_PREFIX = "<-- "
private const val EX_PREFIX = "<X- "
private const val INDENT = "|   "
private const val BRANCH = "|"
private const val NANOS_PER_MILLI = 1_000_000L

/**
 * Thread-local based log trace implementation.
 *
 * Tracks method call hierarchy and execution time using ThreadLocal.
 */
class ThreadLocalLogTrace : LogTrace {

    private val traceIdHolder = ThreadLocal<TraceId>()

    override fun begin(message: String): TraceStatus {
        syncTraceId()
        val traceId = traceIdHolder.get()
        val prefix = buildPrefix(START_PREFIX, traceId.level)

        logger.debug { "# $prefix$message" }

        return TraceStatus(
            traceId = traceId,
            startNanos = System.nanoTime(),
            message = message
        )
    }

    override fun end(status: TraceStatus) = complete(status, null)

    override fun exception(status: TraceStatus, e: Exception) = complete(status, e)

    private fun complete(status: TraceStatus, e: Exception?) {
        val elapsedMs = (System.nanoTime() - status.startNanos) / NANOS_PER_MILLI
        val prefix = buildPrefix(if (e == null) END_PREFIX else EX_PREFIX, status.traceId.level)

        logCompletion(prefix, status.message, elapsedMs, e)
        releaseTraceId()
    }

    private fun logCompletion(prefix: String, message: String, elapsedMs: Long, e: Exception?) {
        when (e) {
            null -> logger.debug { "# $prefix$message , elapsed=${elapsedMs}ms" }
            is KnownException -> logger.debug { "# $prefix$message , elapsed=${elapsedMs}ms" }
            else -> logger.warn { "# $prefix$message , elapsed=${elapsedMs}ms , ex=${e.javaClass.simpleName} ${e.message}" }
        }
    }

    private fun syncTraceId() {
        val current = traceIdHolder.get()
        traceIdHolder.set(current?.nextLevel() ?: TraceId())
    }

    private fun releaseTraceId() {
        val traceId = traceIdHolder.get()
        if (traceId.isFirstLevel) {
            traceIdHolder.remove()
        } else {
            traceIdHolder.set(traceId.prevLevel())
        }
    }

    private fun buildPrefix(prefix: String, level: Int): String =
        (0 until level).joinToString(separator = "") { i ->
            if (i == level - 1) "$BRANCH$prefix" else INDENT
        }
}
