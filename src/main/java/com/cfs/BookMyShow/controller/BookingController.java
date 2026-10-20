package com.cfs.BookMyShow.controller;

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
    @PostMapping
    public ResponseEntity<Booking> addBooking(@RequestBody BookingRequest booking){
        return ResponseEntity.ok(bookingService.createBooking(booking));
    }
@GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
}
@GetMapping("/{userId}")
public ResponseEntity<List<Booking>> getBookingByUserId(@PathVariable Long userId ){
        return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
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
