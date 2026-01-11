package io.glory.commonweb.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import io.glory.common.codes.ResponseCode
import jakarta.servlet.http.HttpServletRequest
import tools.jackson.databind.json.JsonMapper

private val logger = KotlinLogging.logger {}

/**
 * Log error message printer
 */
object ErrorLogPrintUtil {

    private val jsonMapper = JsonMapper.builder().build()

    /**
     * log error message
     *
     * @param request HttpServletRequest
     * @param code ResponseCode
     * @param e Exception
     * @param printTrace log trace stack
     */
    @JvmOverloads
    @JvmStatic
    fun logError(request: HttpServletRequest, code: ResponseCode, e: Exception, printTrace: Boolean = false) {
        val method = request.method
        val requestURI = request.requestURI
        val rootCause = getRootCause(e)

        logErrorV2(method, requestURI, request, code, e, rootCause, printTrace)
    }

    private fun logErrorV2(
        method: String?,
        requestURI: String?,
        request: HttpServletRequest,
        code: ResponseCode,
        exception: Exception,
        rootCause: Throwable,
        printTrace: Boolean
    ) {
        val clientIp = IpAddrUtil.getClientIp(request)
        val serverIp = IpAddrUtil.serverIp
        buildString {
            append("# ==> ERROR INFO ::\n")
            append("RequestURI: $method , $requestURI\n")
            append("ServerIp = $serverIp , ClientIp = $clientIp\n")
            append("Code: [${code.name}] , Message: ${code.message}\n")
            append("Exception: ${exception.javaClass.simpleName} , Cause: ${exception.message}\n")
            append("RootCause: ${rootCause.javaClass.simpleName} , Cause: ${rootCause.message}")
        }.let { if (printTrace) logger.error(exception) { it } else logger.error { it } }
    }

    /**
     * @return root cause of exception
     */
    private tailrec fun getRootCause(throwable: Throwable): Throwable {
        val cause = throwable.cause
        return if (cause != null && cause !== throwable) {
            getRootCause(cause)
        } else {
            throwable
        }
    }

}