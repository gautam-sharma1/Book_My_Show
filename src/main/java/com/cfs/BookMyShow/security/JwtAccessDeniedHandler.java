package com.cfs.BookMyShow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 HANDLES 403 FORBIDDEN = "I know exactly who you are, and you are not allowed."

 THE DIFFERENCE FROM 401 (very common interview question):
   401 Unauthorized -> you are not logged in. Fix: log in.
   403 Forbidden    -> you ARE logged in, but you lack the role. Logging in again
                       will not help; a USER calling DELETE /api/movies/1 gets this.

 Same reason as the entry point: this happens in the filter chain, so your
 GlobalExceptionHandler cannot catch it. We produce the same JSON shape.
*/
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Access denied - your role is not allowed to use this endpoint");
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("path", request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
