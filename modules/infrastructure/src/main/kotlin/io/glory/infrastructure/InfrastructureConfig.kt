package io.glory.infrastructure

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.time.Clock

@Configuration
@EntityScan(basePackages = ["io.glory.infrastructure.persistence", "io.glory.domain"])
@EnableJpaRepositories(basePackages = ["io.glory.infrastructure.persistence", "io.glory.domain"])
class InfrastructureConfig {

    @Bean
    fun clock(): Clock = Clock.systemUTC()
}
