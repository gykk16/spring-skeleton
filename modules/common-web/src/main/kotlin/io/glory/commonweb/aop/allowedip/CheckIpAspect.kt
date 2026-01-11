package io.glory.commonweb.aop.allowedip

import io.github.oshai.kotlinlogging.KotlinLogging
import io.glory.common.annoatations.CheckIp
import io.glory.commonweb.utils.IpAddrUtil
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.core.annotation.Order
import kotlin.reflect.full.findAnnotation

private val logger = KotlinLogging.logger {}

@Aspect
@Order(3)
class CheckIpAspect {

    @Pointcut("@within(io.glory.common.annoatations.CheckIp) || @annotation(io.glory.common.annoatations.CheckIp)")
    fun checkIpsPointcut() {
    }

    @Before("checkIpsPointcut()")
    fun checkClientIp(joinPoint: JoinPoint) {
        logger.debug { "# ==> Check allowed ips..." }

        val annotation = getAnnotation(joinPoint) ?: return
        val clientIp = IpAddrUtil.getClientIp()

        if (!isAllowedIp(clientIp, annotation.allowedIps)) {
            throw AllowedIpException(clientIp)
        }
    }

    private fun getAnnotation(joinPoint: JoinPoint): CheckIp? {
        return joinPoint.signature.declaringType.kotlin.members
            .find { it.name == joinPoint.signature.name }
            ?.findAnnotation<CheckIp>()
            ?: joinPoint.target::class.findAnnotation()
    }

    private fun isAllowedIp(clientIp: String, allowedIps: Array<String>): Boolean {
        return when {
            allowedIps.contains("*") -> {
                logger.debug { "# ==> All IPs are allowed" }
                true
            }

            IpWhitelist.isWhitelisted(clientIp) -> {
                logger.debug { "# ==> Whitelisted IP: $clientIp" }
                true
            }

            allowedIps.contains(clientIp) -> {
                logger.debug { "# ==> Allowed IP: $clientIp" }
                true
            }

            else -> false
        }
    }

    init {
        logger.info { "# ==> ${this.javaClass.simpleName} initialized" }
    }
}