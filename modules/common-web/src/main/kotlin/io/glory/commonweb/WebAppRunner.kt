package io.glory.commonweb

import io.github.oshai.kotlinlogging.KotlinLogging
import io.glory.commonweb.utils.EnvironmentUtil
import io.glory.commonweb.utils.IpAddrUtil
import io.glory.commonweb.utils.LogUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class WebAppRunner {

    private val logger = KotlinLogging.logger {}
    private val jsonMapper = JsonMapper.builder().build()

    @Value("\${spring.application.version:}")
    private val appVersion: String = ""

    @Value("\${server.port:}")
    private val port: String = ""

    @Bean
    fun run(environmentUtil: EnvironmentUtil): CommandLineRunner {
        return CommandLineRunner { _: Array<String> ->
            logger.trace { LogUtil.LOG_LINE }
            jsonMapper.writeValueAsString(
                mapOf(
                    "AppVersion" to appVersion,
                    "ActiveProfile" to environmentUtil.activeProfile(),
                    "ActiveProfiles" to environmentUtil.activeProfiles(),
                    "ServerIp" to IpAddrUtil.serverIp,
                    "Port" to port,
                )
            ).let { logger.info { it } }
            logger.info { "# ==> APP START COMPLETE!!!" }
            logger.trace { LogUtil.LOG_LINE }
        }
    }

}