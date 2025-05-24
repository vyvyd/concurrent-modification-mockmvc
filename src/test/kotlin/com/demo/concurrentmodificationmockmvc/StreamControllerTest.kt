package com.demo.concurrentmodificationmockmvc

import com.demo.concurrentmodificationmockmvc.controllers.StreamController
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StreamController::class)
@Import(SecurityConfigForTesting::class) // Ensure security is disabled
class StreamControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @RepeatedTest(10000)
    fun `should stream chunks and complete with done`() {
        val mvcResult = mockMvc.perform(get("/stream"))
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect {
                status().isOk()
                content().contentType(MediaType.TEXT_PLAIN)
                content().string(containsString("Chunk 0"))
                content().string(containsString("Done"))
            }
    }
}