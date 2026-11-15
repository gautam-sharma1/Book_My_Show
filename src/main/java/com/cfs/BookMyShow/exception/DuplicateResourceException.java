package com.cfs.BookMyShow.exception;

// Thrown when someone tries to create something that must be unique but already
// exists - right now that's registering with an email that's already taken.
// GlobalExceptionHandler maps this to 409 CONFLICT: the request is well-formed, it
// just conflicts with something that already exists on the server.
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
