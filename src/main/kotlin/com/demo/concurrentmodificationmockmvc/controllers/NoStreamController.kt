package com.demo.concurrentmodificationmockmvc.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NoStreamController {

    @GetMapping("/")
    fun sayHello(): String {
        return "Hello, World!"
    }
}