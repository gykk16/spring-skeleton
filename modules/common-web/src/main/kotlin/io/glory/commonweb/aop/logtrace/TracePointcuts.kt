package io.glory.commonweb.aop.logtrace

import org.aspectj.lang.annotation.Pointcut

/**
 * Pointcut definitions for log tracing.
 *
 * Traces:
 * - All `*Controller` classes
 * - All `*Service` classes
 * - Methods annotated with `@LogTrace`
 *
 * Excludes methods annotated with `@ExcludeLogTrace`.
 */
class TracePointcuts {

    @Pointcut("@annotation(io.glory.common.annoatations.ExcludeLogTrace)")
    fun excludeLogTraceAnnotation() = Unit

    @Pointcut("@annotation(io.glory.common.annoatations.LogTrace)")
    fun logTraceAnnotation() = Unit

    @Pointcut("execution(* io.glory..*Controller.*(..))")
    fun allController() = Unit

    @Pointcut("execution(* io.glory..*Service.*(..))")
    fun allService() = Unit

    @Pointcut("execution(* io.glory..*Repository.*(..))")
    fun allRepository() = Unit

    @Pointcut("(allController() || allService() || logTraceAnnotation()) && !excludeLogTraceAnnotation()")
    fun all() = Unit

}
