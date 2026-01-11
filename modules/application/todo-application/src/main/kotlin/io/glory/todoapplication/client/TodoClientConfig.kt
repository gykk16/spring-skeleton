package io.glory.todoapplication.client

import io.glory.infrastructure.client.HttpLoggingInterceptor
import io.glory.infrastructure.client.HttpLoggingInterceptor.Level
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer
import org.springframework.web.service.registry.ImportHttpServices

@Configuration
@ImportHttpServices(group = "jsonplaceholder", types = [TodoClient::class])
class TodoClientConfig {

    @Bean
    fun todoClientGroupConfigurer(): RestClientHttpServiceGroupConfigurer {
        return RestClientHttpServiceGroupConfigurer { groups ->
            groups.filterByName("jsonplaceholder")
                .forEachClient { _, builder ->
                    builder.baseUrl("https://jsonplaceholder.typicode.com")
                        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .requestInterceptor(HttpLoggingInterceptor("TodoClient", Level.SIMPLE))
                }
        }
    }
}
