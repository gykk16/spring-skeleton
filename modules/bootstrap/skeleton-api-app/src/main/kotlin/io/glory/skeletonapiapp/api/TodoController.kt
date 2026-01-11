package io.glory.skeletonapiapp.api

import io.glory.common.annoatations.LogResponseBody
import io.glory.commonweb.response.resource.ApiResource
import io.glory.todoapplication.service.TodoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/todos")
class TodoController(
    private val todoService: TodoService
) {

    @GetMapping
    fun findAll() = ApiResource.success(todoService.findAll())

    @GetMapping("/{id}")
    @LogResponseBody
    fun findById(@PathVariable id: Int) = ApiResource.success(todoService.findById(id))

}