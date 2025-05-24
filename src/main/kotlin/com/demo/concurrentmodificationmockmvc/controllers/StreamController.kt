package com.demo.concurrentmodificationmockmvc.controllers

import java.io.OutputStream
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody

@RestController
class StreamController {

    @GetMapping("/stream")
    fun stream(): ResponseEntity<StreamingResponseBody> {
        val responseBody = StreamingResponseBody { outputStream: OutputStream ->
            val writer = outputStream.bufferedWriter()
            repeat(10) { i ->
                writer.write("Chunk $i\n")
                writer.flush()
            }
            writer.write("Done\n")
            writer.flush()
        }

        return ResponseEntity.ok()
            .header("Content-Type", "text/plain")
            .body(responseBody)
    }
}