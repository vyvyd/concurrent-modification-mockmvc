package com.demo.concurrentmodificationmockmvc

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
class SecurityConfigForTesting {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { it
                .anyRequest().permitAll() // Allow all requests
            }
            .csrf { it.disable() } // Disable CSRF
            .formLogin { it.disable() } // Disable form login
            .httpBasic { it.disable() } // Disable HTTP Basic auth
            .headers {
                    headers -> headers.disable()
            }  // disable frame options header (used in tests for DEMO)

        return http.build()
    }
}