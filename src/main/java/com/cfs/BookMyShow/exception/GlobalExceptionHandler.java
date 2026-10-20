package com.cfs.BookMyShow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

                                                                                                                            // the meaning of whole handleRuntimeException method is the runtime exception thrown from anywhere from the project ,all the exception we throw from:- throw new RuntimeException(" ergheig..somthing")  will come in this block and  all thing which we put in error ,will show
                                                                                                                      // so in whole project from anywhere we throw runtimeexception   so yeh block run  and  things are show or this things are return same way we write it down for Exception
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
