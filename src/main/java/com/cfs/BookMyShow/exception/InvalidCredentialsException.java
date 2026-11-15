package com.cfs.BookMyShow.exception;

// Thrown when login fails - wrong email, or an email whose password does not match.
// GlobalExceptionHandler maps this to 401 UNAUTHORIZED, which is the correct status
// for "I don't believe you are who you say you are" (403 would mean "I know who you
// are, but you aren't allowed to do this" - that is AccessDeniedException's job).
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
