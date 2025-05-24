package com.demo.concurrentmodificationmockmvc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
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
            }  // disable frame options header

        return http.build()
    }
}