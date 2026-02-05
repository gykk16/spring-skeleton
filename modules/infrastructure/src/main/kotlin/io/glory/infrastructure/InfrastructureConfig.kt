package io.glory.infrastructure

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = ["io.glory.infrastructure.persistence"])
@EnableJpaRepositories(basePackages = ["io.glory.infrastructure.persistence"])
class InfrastructureConfig
