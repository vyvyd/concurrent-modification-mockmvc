# ConcurrentModificationException | MockMvc | Spring Security

## Problem 
If your Spring Boot project has the following setup:

- A controller endpoint returning a StreamingResponseBody
    
- A @WebMvcTest using MockMvc

- Spring Security is enabled during the test

Then you might sometimes get a `ConcurrentModificationException` when running the test.

This doesn't happen every time – it's a bit random. If you use JUnit's @RepeatedTest, it becomes easier to catch the issue, since running the test multiple times increases the chances of the error showing up.

## References 

The problem is well known.

https://github.com/spring-projects/spring-framework/issues/31543  
https://github.com/spring-projects/spring-security/issues/9175

## Deep Dive 

From the exception stack-trace that is thrown during the ConcurrentModificationException, it is very clear that the problem happens because a Spring Security Filter in the Filter Chain is writing headers to the MockMvcResponse. 

The `ConcurrentModificationException` is thrown because the headers are stored in a LinkedCaseInsensitiveMap, which is not thread-safe. 

This is the cause of the problem. 

Since the problem is not something that we as a consumer of these libraries can solve easily. 

I would propose that we disable the Spring-Security HeaderWriterFilter + other 'header writing' filters within the scope of the `@WebMvcTest`.
