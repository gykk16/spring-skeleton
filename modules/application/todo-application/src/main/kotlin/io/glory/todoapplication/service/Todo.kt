package io.glory.todoapplication.service

import io.glory.todoapplication.client.dto.TodoDto

data class Todo(
    val id: Int,
    val userId: Int,
    val title: String,
    val completed: Boolean
) {
    companion object {
        fun from(dto: TodoDto): Todo = Todo(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            completed = dto.completed
        )
    }
}
