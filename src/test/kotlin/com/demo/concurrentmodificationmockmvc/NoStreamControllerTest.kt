package com.demo.concurrentmodificationmockmvc

import com.demo.concurrentmodificationmockmvc.controllers.NoStreamController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(NoStreamController::class)
@Import(SecurityConfig::class)
class NoStreamControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should return hello world`() {
        mockMvc.get("/")
            .andExpect {
                status { isOk() }
                content { string("Hello, World!") }
            }
    }
}