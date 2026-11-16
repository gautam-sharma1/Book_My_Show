package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.entity.Screen;
import com.cfs.BookMyShow.entity.Seat;
import com.cfs.BookMyShow.repository.SeatRepository;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class  SeatService {
    private final SeatRepository seatRepository;
    private final ScreenService screenService;

    public Seat addSeat(Seat seat){
       return seatRepository.save(seat);

    }
    public List<Seat> getSeatByScreenId(Long screenId){
      return seatRepository.findByScreenId(screenId);
    }
    public Seat getSeatById(Long seatId){
     // OLDER CODE: return   seatRepository.findById(seatId).orElseThrow(()-> new RuntimeException("  seat not found by id "+seatId));
     return   seatRepository.findById(seatId).orElseThrow(()-> new ResourceNotFoundException("  seat not found by id "+seatId)); // NEW: answers 404 Not Found

    }
}
