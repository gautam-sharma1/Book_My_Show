package com.cfs.BookMyShow.exception;

// The general-purpose "this request cannot be carried out" exception - invalid seat
// ids, cancelling a booking that is already cancelled, a new screen sent in with an
// id already set, etc. GlobalExceptionHandler maps this to 400 BAD REQUEST, the same
// status plain RuntimeException got before - so behaviour for these cases is unchanged,
// only the exception type naming is clearer.
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
