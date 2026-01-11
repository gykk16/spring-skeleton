package io.glory.commonweb.aop

import io.glory.commonweb.aop.allowedip.CheckIpAspect
import io.glory.commonweb.aop.logtrace.LogTrace
import io.glory.commonweb.aop.logtrace.LogTraceAspect
import io.glory.commonweb.aop.logtrace.ThreadLocalLogTrace
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AopConfig {

    @Bean
    fun logTrace(): LogTrace = ThreadLocalLogTrace()

    @Bean
    fun logTraceAspect(): LogTraceAspect = LogTraceAspect(logTrace())

    @Bean
    fun allowedIpAspect(): CheckIpAspect = CheckIpAspect()
}
