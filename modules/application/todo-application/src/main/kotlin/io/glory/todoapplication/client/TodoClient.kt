package io.glory.todoapplication.client

import io.glory.todoapplication.client.dto.TodoDto
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.DeleteExchange
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.annotation.PutExchange

@HttpExchange("/todos")
interface TodoClient {

    @GetExchange
    fun fetchAll(): List<TodoDto>

    @GetExchange("/{id}")
    fun findById(@PathVariable id: Int): TodoDto

    @PostExchange
    fun create(@RequestBody todo: TodoDto): TodoDto

    @PutExchange("/{id}")
    fun update(@PathVariable id: Int, @RequestBody todo: TodoDto): TodoDto

    @DeleteExchange("/{id}")
    fun delete(@PathVariable id: Int)
}
