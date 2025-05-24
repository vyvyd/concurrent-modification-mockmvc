# Concurrent Modification Exception with MockMVCTests, and Spring Security 

## Problem 
If you have a project contains

- a controller endpoint that responds with a `[StreamingResponseBody](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBody.html)`
- a `@WebMvcTest` that utilizes `MockMvc`
- spring-security configured to be used during the test

Then you might see occassional `ConcurrentModificationExceptions` being thrown from the test. 

This is not consistent.
