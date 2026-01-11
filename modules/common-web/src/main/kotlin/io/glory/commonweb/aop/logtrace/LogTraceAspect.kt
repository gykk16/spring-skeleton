package io.glory.commonweb.aop.logtrace

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order

private val logger = KotlinLogging.logger {}

@Aspect
@Order(2)
class LogTraceAspect(
    private val logTrace: LogTrace
) {

    init {
        logger.info { "# ==> ${javaClass.simpleName} initialized" }
    }

    @Around("io.glory.commonweb.aop.logtrace.TracePointcuts.all()")
    fun traceMethod(joinPoint: ProceedingJoinPoint): Any? {
        val status = logTrace.begin(joinPoint.signature.toShortString())

        return try {
            val result = joinPoint.proceed()
            logTrace.end(status)
            result
        } catch (e: Exception) {
            logTrace.exception(status, e)
            throw e
        }
    }
}
