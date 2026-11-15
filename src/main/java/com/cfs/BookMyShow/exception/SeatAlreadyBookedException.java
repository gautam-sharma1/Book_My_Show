package com.cfs.BookMyShow.exception;

// Thrown when a seat someone is trying to book has already been taken by another
// booking. GlobalExceptionHandler maps this to 409 CONFLICT - same reasoning as
// DuplicateResourceException: the request is valid, it just lost the race for that seat.
public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(String message) {
        super(message);
    }
}
