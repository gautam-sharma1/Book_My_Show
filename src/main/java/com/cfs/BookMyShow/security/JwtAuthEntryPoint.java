package com.cfs.BookMyShow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 HANDLES 401 UNAUTHORIZED = "I don't know who you are."
 (no token sent, token expired, or token is fake)

 WHY DOES THIS CLASS NEED TO EXIST?
 Your GlobalExceptionHandler (@RestControllerAdvice) only catches errors thrown
 INSIDE controller methods. But a security failure happens earlier - in the filter
 chain, before any controller is reached. So @RestControllerAdvice never sees it,
 and without this class the user gets Tomcat's ugly default HTML error page
 instead of your clean JSON.

 So here we build the SAME shape your GlobalExceptionHandler already returns:
 { timestamp, message, status }
*/
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    // ObjectMapper is the Jackson object that turns a Java Map into a JSON string.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();   // needed so it knows how to write LocalDateTime

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 401 = not logged in (different from 403 = logged in but not allowed).
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Authentication required - please login and send the token as: Authorization: Bearer <token>");
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("path", request.getRequestURI());

        // Write the JSON straight onto the response - we are below the controller
        // layer here, so we cannot just "return" an object like a @RestController would.
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
