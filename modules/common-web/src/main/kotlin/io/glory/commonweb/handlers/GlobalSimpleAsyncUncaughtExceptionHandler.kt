package io.glory.commonweb.handlers

import io.github.oshai.kotlinlogging.KotlinLogging
import io.glory.common.exceptions.KnownException
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class GlobalSimpleAsyncUncaughtExceptionHandler : SimpleAsyncUncaughtExceptionHandler() {

    private val logger = KotlinLogging.logger {}

    override fun handleUncaughtException(
        ex: Throwable,
        method: Method,
        vararg params: Any?
    ) {
        if (ex is KnownException) {
            logger.debug(ex) { ex.simplePrint() }
        } else {
            super.handleUncaughtException(ex, method, *params)
        }
    }

    init {
        logger.info { "# ==> ${this.javaClass.simpleName} initialized" }
    }

}