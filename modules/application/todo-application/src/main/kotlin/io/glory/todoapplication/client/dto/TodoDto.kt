package io.glory.todoapplication.client.dto

@JvmRecord
data class TodoDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val completed: Boolean = false
)
