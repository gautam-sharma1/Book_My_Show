package com.cfs.BookMyShow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.exception.DuplicateResourceException;
import com.cfs.BookMyShow.exception.SeatAlreadyBookedException;
import com.cfs.BookMyShow.exception.InvalidRequestException;
import com.cfs.BookMyShow.exception.InvalidCredentialsException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

                                                                                                                            // the meaning of whole handleRuntimeException method is the runtime exception thrown from anywhere from the project ,all the exception we throw from:- throw new RuntimeException(" ergheig..somthing")  will come in this block and  all thing which we put in error ,will show
                                                                                                                      // so in whole project from anywhere we throw runtimeexception   so yeh block run  and  things are show or this things are return same way we write it down for Exception
    // NEW: handles @Valid failures on a @RequestBody.
    //
    // When a DTO annotated with @NotBlank / @Email / @Positive fails, Spring throws
    // MethodArgumentNotValidException BEFORE your controller method ever runs. Without
    // this handler the client gets Spring's enormous default error blob. Here we turn it
    // into a small map of  fieldName -> message  so a frontend can show the message
    // under the right input box.
    //
    // 400 Bad Request is the correct status: the request really is malformed.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException e){
        Map<String,Object> fieldErrors = new HashMap<>();
        // getBindingResult() holds one FieldError per broken rule.
        e.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage()));

        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", "Validation failed");
        error.put("errors", fieldErrors);
        error.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(error);
    }

    // NEW: ownership / permission failures thrown from inside a service.
    //
    // WHY THIS MUST COME FIRST: the generic @ExceptionHandler(Exception.class) below
    // would otherwise catch AccessDeniedException too and answer 400. Spring picks the
    // MOST SPECIFIC handler for the thrown type, so declaring this one makes permission
    // errors answer 403 Forbidden, matching what JwtAccessDeniedHandler returns for
    // URL-level denials. Same meaning, same status code, wherever the check happened.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAccessDenied(AccessDeniedException e){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // NEW: a findById()/findByEmail() lookup came back empty - movie, show, booking,
    // user, etc. does not exist. 404 is correct here, not 400: the request was fine,
    // the thing it asked for just isn't there. Must be declared before the generic
    // RuntimeException handler below, same reasoning as AccessDeniedException above -
    // Spring picks the most specific handler, but keeping specific ones listed first
    // is what makes this class readable top to bottom.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(ResourceNotFoundException e){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // NEW: someone tried to create something that must be unique but already exists
    // (registering with a taken email), or lost a race for a seat another booking just
    // took. Both are "request is valid, it conflicts with existing data" - 409 CONFLICT.
    @ExceptionHandler({DuplicateResourceException.class, SeatAlreadyBookedException.class})
    public ResponseEntity<Map<String,Object>> handleConflict(RuntimeException e){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // NEW: login failed - wrong email, or email/password mismatch. 401 UNAUTHORIZED
    // is the correct status for "I don't believe who you say you are" (403 is for
    // "I know who you are, but you can't do this" - that's AccessDeniedException).
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidCredentials(InvalidCredentialsException e){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // NEW: InvalidRequestException is the renamed, specific version of what used to
    // just be a plain RuntimeException (invalid seat ids, cancelling an already-cancelled
    // booking, etc). Still 400 BAD REQUEST - behaviour for these is unchanged, only the
    // exception type is clearer. Falls through to handleRuntimeException below anyway
    // since it extends RuntimeException, but declared explicitly for clarity.
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidRequest(InvalidRequestException e){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(error);
    }

    // OLD: this used to be the ONLY handler catching every throw new RuntimeException(...)
    // in the project, which is why every error used to come back as 400 regardless of
    // what actually went wrong. Kept as the fallback for any plain RuntimeException that
    // doesn't match one of the specific types above.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntimeException(RuntimeException e){
    Map<String,Object> error = new HashMap<>();                                                                         // so ham use denge n ki error mae kya hua hai
    error.put("timestamp", LocalDateTime.now());                                                                       // toh use ham batayenge ki error kab ayi hai
    error.put("message", e.getMessage());                                                                             // jo message ham wahan se bhejenge na woh yahan automatically ajayegi  kahan se throw new RuntimeException("fdghbgkf....") this message
    error.put("status", HttpStatus.BAD_REQUEST.value());                                                                     // ab us sense mae ham ek status code bhej denge
    return ResponseEntity.badRequest().body(error);                                                                       // ab ku ki method  ka return type ResponseEntity hai toh yeh line se ham pura metho bhej denge like  msg,timestamp,status which we have done above above
    }
                                                                                                                                   // the meaning of whole handleException method is the  exception thrown from anywhere from the project ,all the exception we throw from:- throw new Exception(" ergheig..somthing")  will come in this block and  all thing which we put in error ,will show
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleException(Exception e){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message"+" something went wrong ", e.getMessage());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.badRequest().body(error);
    }
}
