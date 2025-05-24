# ConcurrentModificationException | MockMvc | Spring Security

## Problem 
If you have a project contains

- a controller endpoint that responds with a `[StreamingResponseBody](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBody.html)`
- a `@WebMvcTest` that utilizes `MockMvc`
- spring-security configured to be used during the test

Then you might see occassional `ConcurrentModificationExceptions` being thrown from the test. 

This is not consistent, and occurs only randomly. As such, a `@RepeatedTest` JUnit annotation will show the problem really easily.

## References 

The problem is well known.

https://github.com/spring-projects/spring-framework/issues/31543
https://github.com/spring-projects/spring-security/issues/9175

## Solution 

From the exception stack-trace that is thrown during the ConcurrentModificationException, it is very clear that the problem happens because a Spring Security Filter in the Filter Chain is writing headers to the MockMvcResponse. However, headers are stored in a LinkedCaseInsensitiveMap, which is not thread-safe. 

This is the cause of the problem. 

Since the problem is not something that we as a consumer of these libraries can solve easily. 

I would propose that we disable the Spring-Security HeaderWriterFilter + other 'header writing' filters within the scope of the `@WebMvcTest`.
