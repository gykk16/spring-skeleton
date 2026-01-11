package io.glory.todoapplication.service

import io.glory.common.codes.response.ErrorCode
import io.glory.common.exceptions.KnownException
import io.glory.todoapplication.client.TodoClient
import io.glory.todoapplication.client.dto.TodoDto
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val todoClient: TodoClient
) {

    fun findAll(): List<Todo> {
        return todoClient.fetchAll().map { Todo.from(it) }
    }

    fun findById(id: Int): Todo {
        return runCatching { todoClient.findById(id) }
            .map { Todo.from(it) }
            .getOrElse { throw TodoNotFoundException(id) }
    }

    fun create(title: String, userId: Int): Todo {
        val dto = TodoDto(id = 0, userId = userId, title = title, completed = false)
        return Todo.from(todoClient.create(dto))
    }

    fun update(id: Int, title: String, completed: Boolean): Todo {
        val existing = findById(id)
        val dto = TodoDto(id = id, userId = existing.userId, title = title, completed = completed)
        return Todo.from(todoClient.update(id, dto))
    }

    fun delete(id: Int) {
        todoClient.delete(id)
    }
}

class TodoNotFoundException(id: Int) : KnownException(ErrorCode.DATA_NOT_FOUND, "Todo not found: $id")
