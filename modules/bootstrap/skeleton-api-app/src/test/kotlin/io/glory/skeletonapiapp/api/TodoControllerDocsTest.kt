package io.glory.skeletonapiapp.api

import io.glory.testsupport.restdocs.DocsFieldType.*
import io.glory.testsupport.restdocs.RestDocsSupport
import io.glory.testsupport.restdocs.type
import io.glory.todoapplication.service.Todo
import io.glory.todoapplication.service.TodoService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TodoControllerDocsTest : RestDocsSupport() {

    private val todoService: TodoService = mock()

    override fun initController(): Any = TodoController(todoService)

    @Test
    fun `find all todos`(): Unit {
        // given
        val todos = listOf(
            Todo(id = 1, userId = 1, title = "delectus aut autem", completed = false),
            Todo(id = 2, userId = 1, title = "quis ut nam facilis", completed = true),
        )
        given(todoService.findAll()).willReturn(todos)

        // when & then
        mockMvc.perform(get("/api/v1/todos"))
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    responseFields(*responseArrayCommonFieldsSubsection()),
                    dataResponseFields(
                        "id" type NUMBER means "Todo ID" example "1",
                        "userId" type NUMBER means "User ID" example "1",
                        "title" type STRING means "Todo title" example "delectus aut autem",
                        "completed" type BOOLEAN means "Completion status" example "false",
                    ),
                )
            )
    }

    @Test
    fun `find todo by id`(): Unit {
        // given
        val todo = Todo(id = 1, userId = 1, title = "delectus aut autem", completed = false)
        given(todoService.findById(any())).willReturn(todo)

        // when & then
        mockMvc.perform(get("/api/v1/todos/{id}", 1))
            .andExpect(status().isOk)
            .andDo(
                restDocs.document(
                    pathParameters(
                        parameterWithName("id").description("Todo ID"),
                    ),
                    responseFields(*responseCommonFieldsSubsection()),
                    dataResponseFields(
                        "id" type NUMBER means "Todo ID" example "1",
                        "userId" type NUMBER means "User ID" example "1",
                        "title" type STRING means "Todo title" example "delectus aut autem",
                        "completed" type BOOLEAN means "Completion status" example "false",
                    ),
                )
            )
    }
}
