package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.BookingRequest;
import com.cfs.BookMyShow.entity.Booking;
import com.cfs.BookMyShow.entity.Seat;
import com.cfs.BookMyShow.entity.Show;
import com.cfs.BookMyShow.entity.User;
import com.cfs.BookMyShow.enums.BookingStatus;
import com.cfs.BookMyShow.repository.BookingRepository;
import com.cfs.BookMyShow.repository.ScreenRepository;
import com.cfs.BookMyShow.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserService userService;
    private final ShowService showService;

    public Booking createBooking(BookingRequest bookingRequest){
        User user = userService.getUserById(bookingRequest.getUserId());
        Show show = showService.getShowById(bookingRequest.getShowId());
        //checking if any of the requested seats are already booked
        List<Long> alreadyBookedseat = bookingRepository.findBookedSeatIdsByShowId(show.getId());

 for(Long seatId:bookingRequest.getSeatIds()){
    if(alreadyBookedseat.contains(seatId)){
        throw new RuntimeException("Seat with Id"+seatId+ "is already booked");
    }

 }
 List<Seat> seats = seatRepository.findAllById(bookingRequest.getSeatIds());
        if(seats.size()!= bookingRequest.getSeatIds().size()){
            throw new RuntimeException("some seatsare Invalid");
        }
        double totalPrice = seats.size()*show.getTicketPrice();
        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .seats(seats)
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .build();
        return bookingRepository.save(booking);
}

public Booking getBookingById(Long bookingId){
 return bookingRepository.findById(bookingId).orElseThrow(()-> new RuntimeException("booking id not found by id "+bookingId));

}
public List<Booking> getBookingByUserId(Long userId){// which user has how many booking
   return bookingRepository.findByUserId(userId);

}
public Booking CancelBooking(Long BookingId){
    Booking booking = getBookingById(BookingId);
    booking.setStatus(BookingStatus.CANCELLED);
return bookingRepository.save(booking);
}
public List<Seat> getAvailableSeatsbyShowId(Long showId){
    Show show = showService.getShowById(showId);
    List<Seat> allSeats = seatRepository.findByScreenId(show.getScreen().getId());
  List <Long> bookingSeatIds=  bookingRepository.findBookedSeatIdsByShowId(showId);
return allSeats.stream().filter(seat -> !bookingSeatIds.contains(seat.getId())).toList();
}
}
