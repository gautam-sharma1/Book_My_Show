package com.cfs.BookMyShow.controller;

import jakarta.validation.Valid;
import com.cfs.BookMyShow.dto.BookingRequest;
import com.cfs.BookMyShow.entity.Booking;
import com.cfs.BookMyShow.entity.Seat;
import com.cfs.BookMyShow.repository.BookingRepository;
import com.cfs.BookMyShow.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    // The request body is now just {"showId":1,"seatIds":[10,11]} - no userId.
    // Who is booking comes from the Authorization: Bearer <token> header instead.
    @PostMapping
    public ResponseEntity<Booking> addBooking(@Valid @RequestBody BookingRequest booking){
        return ResponseEntity.ok(bookingService.createBooking(booking));
    }
@GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
}
// OLDER CODE (the user id came from the URL, so any logged-in person could read
// anyone else's bookings just by changing the number: /api/booking/user/5).
// Kept for reference:
// @GetMapping("/user/{userId}")
// public ResponseEntity<List<Booking>> getBookingByUserId(@PathVariable Long userId ){
//         return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
// }

// NEW: "my" needs no id at all. The service reads who you are from the token, so
// there is simply no number left for anyone to tamper with.
// Notice how removing the parameter is what removes the vulnerability - the safest
// input is the one you never accept.
@GetMapping("/my")
public ResponseEntity<List<Booking>> getMyBookings(){
        return ResponseEntity.ok(bookingService.getMyBookings());
}
@PutMapping("/{BookingId}/cancel")
    public ResponseEntity<Booking> CancelBooking(@PathVariable Long BookingId){
        return ResponseEntity.ok(bookingService.CancelBooking(BookingId));
}
@GetMapping("/show/{showId}/available-seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long showId){
        return ResponseEntity.ok(bookingService.getAvailableSeatsbyShowId(showId));
    }
}
