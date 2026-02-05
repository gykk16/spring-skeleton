package io.glory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableCaching
@EnableAsync
class CommonApiApplication

fun main(args: Array<String>) {
    runApplication<CommonApiApplication>(*args)
}
