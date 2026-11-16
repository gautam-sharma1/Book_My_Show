package com.cfs.BookMyShow.controller;

import com.cfs.BookMyShow.entity.Seat;
import com.cfs.BookMyShow.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/seat")
    public ResponseEntity<Seat> addSeats(@RequestBody Seat seat){
        return ResponseEntity.ok(seatService.addSeat(seat));
    }
    @GetMapping("/screen/{ScreenId}")
    public ResponseEntity<List<Seat>> getSeatByScreenId(@PathVariable Long ScreenId){
        return ResponseEntity.ok(seatService.getSeatByScreenId(ScreenId));
    }
    @GetMapping("/{screenId}")
    public ResponseEntity<Seat> getSeatById(@PathVariable Long screenId){
        return ResponseEntity.ok(seatService.getSeatById(screenId));
    }
}
