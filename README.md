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

## Why does this exception happen?

From the exception stack-trace that is thrown during the ConcurrentModificationException, it is very clear that the problem happens **the moment a Spring Security Filter in the Filter Chain is writing headers to the MockMvcResponse.** 

The `ConcurrentModificationException` is thrown because the headers in the response  are stored in a LinkedCaseInsensitiveMap, which is not thread-safe. A Spring Security Filter tries to write header information to the response from the Servlet Thread, where as the Streaming Response Body is being written from a task-executor thread. That is the concurrent access modification exception that we are seeing.

```

java.util.ConcurrentModificationException
	at java.base/java.util.HashMap.computeIfAbsent(HashMap.java:1221)
	at org.springframework.util.LinkedCaseInsensitiveMap.computeIfAbsent(LinkedCaseInsensitiveMap.java:239)
	at org.springframework.util.LinkedCaseInsensitiveMap.computeIfAbsent(LinkedCaseInsensitiveMap.java:50)
	at org.springframework.mock.web.MockHttpServletResponse.doAddHeaderValue(MockHttpServletResponse.java:776)
	at org.springframework.mock.web.MockHttpServletResponse.addHeaderValue(MockHttpServletResponse.java:731)
	at org.springframework.mock.web.MockHttpServletResponse.addHeader(MockHttpServletResponse.java:699)
	at jakarta.servlet.http.HttpServletResponseWrapper.addHeader(HttpServletResponseWrapper.java:141)
	at org.springframework.security.web.firewall.FirewalledResponse.addHeader(FirewalledResponse.java:60)
	at jakarta.servlet.http.HttpServletResponseWrapper.addHeader(HttpServletResponseWrapper.java:141)
	at org.springframework.security.web.header.writers.StaticHeadersWriter.writeHeaders(StaticHeadersWriter.java:64)
	at org.springframework.security.web.header.HeaderWriterFilter.writeHeaders(HeaderWriterFilter.java:99)
	at org.springframework.security.web.header.HeaderWriterFilter$HeaderWriterResponse.writeHeaders(HeaderWriterFilter.java:132)
	at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:93)
	at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:75)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:374)
	at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:82)
	at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:69)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:374)
	at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:62)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:374)
	at org.springframework.security.web.session.DisableEncodeUrlFilter.doFilterInternal(DisableEncodeUrlFilter.java:42)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
	at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:374)
	at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:233)
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:191)
	at org.springframework.web.filter.CompositeFilter$VirtualFilterChain.doFilter(CompositeFilter.java:113)
	at org.springframework.web.filter.ServletRequestPathFilter.doFilter(ServletRequestPathFilter.java:52)
	at org.springframework.web.filter.CompositeFilter$VirtualFilterChain.doFilter(CompositeFilter.java:113)
	at org.springframework.web.filter.CompositeFilter.doFilter(CompositeFilter.java:74)
	at org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration$CompositeFilterChainProxy.doFilter(WebSecurityConfiguration.java:319)
	at org.springframework.web.filter.CompositeFilter$VirtualFilterChain.doFilter(CompositeFilter.java:113)
	at org.springframework.web.servlet.handler.HandlerMappingIntrospector.lambda$createCacheFilter$3(HandlerMappingIntrospector.java:243)
	at org.springframework.web.filter.CompositeFilter$VirtualFilterChain.doFilter(CompositeFilter.java:113)
	at org.springframework.web.filter.CompositeFilter.doFilter(CompositeFilter.java:74)
	at org.springframework.security.config.annotation.web.configuration.WebMvcSecurityConfiguration$CompositeFilterChainProxy.doFilter(WebMvcSecurityConfiguration.java:240)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:362)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:278)
	at org.springframework.test.web.servlet.setup.MockMvcFilterDecorator.doFilter(MockMvcFilterDecorator.java:162)
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:132)
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
	at org.springframework.test.web.servlet.setup.MockMvcFilterDecorator.doFilter(MockMvcFilterDecorator.java:162)
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:132)
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
	at org.springframework.test.web.servlet.setup.MockMvcFilterDecorator.doFilter(MockMvcFilterDecorator.java:162)
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:132)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)
	at org.springframework.test.web.servlet.setup.MockMvcFilterDecorator.doFilter(MockMvcFilterDecorator.java:162)
	at org.springframework.mock.web.MockFilterChain.doFilter(MockFilterChain.java:132)
	at org.springframework.test.web.servlet.MockMvc.perform(MockMvc.java:201)
	at com.demo.concurrentmodificationmockmvc.StreamControllerTest.should stream chunks and complete with done(StreamControllerTest.kt:25)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
	at java.base/java.util.stream.IntPipeline$1$1.accept(IntPipeline.java:180)
	at java.base/java.util.stream.Streams$RangeIntSpliterator.forEachRemaining(Streams.java:104)
	at java.base/java.util.Spliterator$OfInt.forEachRemaining(Spliterator.java:711)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:596)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
```

## Solution

Since the problem is not something that we as a consumer of these libraries can solve easily. 

I would propose that we disable the Spring-Security HeaderWriterFilter + other 'header writing' filters within the scope of the `@WebMvcTest`. This can be done through a custom Spring Security Configuration used only within the scope of `@WebMvcTest` instances.

```
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
```
