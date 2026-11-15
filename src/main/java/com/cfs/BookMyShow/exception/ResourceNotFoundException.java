package com.cfs.BookMyShow.exception;

// Thrown whenever a findById()/findByEmail() lookup comes back empty - a movie, show,
// booking, user, etc. that was asked for does not exist. GlobalExceptionHandler maps
// this to 404 NOT FOUND, instead of the 400 BAD REQUEST a plain RuntimeException got.
// 404 is the correct status here: the request itself was fine, the thing it asked for
// just isn't there.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
