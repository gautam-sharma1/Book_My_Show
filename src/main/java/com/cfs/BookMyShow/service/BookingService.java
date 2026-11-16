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
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.exception.SeatAlreadyBookedException;
import com.cfs.BookMyShow.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import com.cfs.BookMyShow.enums.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserService userService;
    private final ShowService showService;

    // @Transactional = check and save happen in ONE transaction, so the lock below is held
    // until the booking is saved. Without it the lock would be released immediately.
    @Transactional
    public Booking createBooking(BookingRequest bookingRequest){
        // OLDER CODE (trusted the userId the client put in the request body, so anyone
        // could book tickets on someone else's account). Kept for reference:
        //        User user = userService.getUserById(bookingRequest.getUserId());

        // NEW: the booking belongs to whoever owns the token, full stop.
        User user = getLoggedInUser();
        Show show = showService.getShowByIdForUpdate(bookingRequest.getShowId()); // locked fetch -> other threads booking this show wait here
        //checking if any of the requested seats are already booked
        List<Long> alreadyBookedseat = bookingRepository.findBookedSeatIdsByShowId(show.getId());

 for(Long seatId:bookingRequest.getSeatIds()){
    if(alreadyBookedseat.contains(seatId)){
        // OLDER CODE (plain RuntimeException answered 400 Bad Request - wrong status,
        // the request was fine, it just lost a race for the seat). Kept for reference:
        // throw new RuntimeException("Seat with Id"+seatId+ "is already booked");
        throw new SeatAlreadyBookedException("Seat with Id"+seatId+ "is already booked"); // NEW: answers 409 Conflict
    }

 }
 List<Seat> seats = seatRepository.findAllById(bookingRequest.getSeatIds());
        if(seats.size()!= bookingRequest.getSeatIds().size()){
            // OLDER CODE: throw new RuntimeException("some seatsare Invalid");
            throw new InvalidRequestException("some seatsare Invalid"); // NEW: still 400, clearer exception type
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
 // OLDER CODE: Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new RuntimeException("booking id not found by id "+bookingId));
 Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("booking id not found by id "+bookingId)); // NEW: answers 404 Not Found
 // NEW: you may only read your own booking (admins may read any).
 checkOwnership(booking, getLoggedInUser());
 return booking;
}

// Internal lookup with NO ownership check, for code that already knows what it is doing.
private Booking findBookingOrThrow(Long bookingId){
 // OLDER CODE: return bookingRepository.findById(bookingId).orElseThrow(()-> new RuntimeException("booking id not found by id "+bookingId));
 return bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("booking id not found by id "+bookingId)); // NEW: answers 404 Not Found
}
// OLDER CODE (took the user id as a parameter and returned that person's bookings
// with NO check that the caller IS that person - so passing someone else's id read
// their whole booking history. Replaced by getMyBookings(), which takes the identity
// from the token instead. Left live by mistake after the controller moved to /my:
// nothing called it any more, but any future controller wired to it would have
// re-opened the hole. Commented out so that cannot happen.) Kept for reference:
// public List<Booking> getBookingByUserId(Long userId){// which user has how many booking
//    return bookingRepository.findByUserId(userId);
//
// }
// OLDER CODE (any logged-out stranger could cancel ANY booking by guessing its id,
// and a cancelled booking could be cancelled again and again). Kept for reference:
// public Booking CancelBooking(Long BookingId){
//     Booking booking = getBookingById(BookingId);
//     booking.setStatus(BookingStatus.CANCELLED);
// return bookingRepository.save(booking);
// }
@Transactional
public Booking CancelBooking(Long BookingId){
    Booking booking = findBookingOrThrow(BookingId);
    // NEW: only the owner (or an admin) may cancel.
    checkOwnership(booking, getLoggedInUser());
    // NEW: cancelling twice is a mistake, not a no-op. Without this guard the seat
    // would already be free and the second call would silently "succeed".
    if(booking.getStatus() == BookingStatus.CANCELLED){
        // OLDER CODE: throw new RuntimeException("This booking is already cancelled");
        throw new InvalidRequestException("This booking is already cancelled"); // NEW: still 400, clearer exception type
    }
    booking.setStatus(BookingStatus.CANCELLED);
return bookingRepository.save(booking);
}
public List<Seat> getAvailableSeatsbyShowId(Long showId){
    Show show = showService.getShowById(showId);
    List<Seat> allSeats = seatRepository.findByScreenId(show.getScreen().getId());
  List <Long> bookingSeatIds=  bookingRepository.findBookedSeatIdsByShowId(showId);
return allSeats.stream().filter(seat -> !bookingSeatIds.contains(seat.getId())).toList();
}

    // ------------------------------------------------------------------
    // WHO IS CALLING? - read the logged-in user out of the security context
    // ------------------------------------------------------------------
    // JwtAuthFilter already read the token and stored the authentication for this
    // request. SecurityContextHolder is simply the box it put it in. Reading from it
    // here is safe because SecurityConfig guarantees these methods are only reachable
    // when somebody is actually logged in.
    //
    // getName() gives back the "username" we set in CustomUserDetailsService, which
    // for us is the email.
    private User getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("You must be logged in to do this");
        }
        return userService.getUserByEmail(authentication.getName());
    }

    // ------------------------------------------------------------------
    // OWNERSHIP CHECK - "is this YOUR booking?"
    // ------------------------------------------------------------------
    // A URL rule in SecurityConfig can say "you must be logged in", but it cannot say
    // "this particular row must belong to you" - that needs the data. So this check
    // lives here in the service, right next to the thing being protected.
    //
    // ADMIN deliberately bypasses the check, because real support staff need to look
    // up and cancel bookings for customers who phone in.
    private void checkOwnership(Booking booking, User currentUser){
        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if(!isOwner && !isAdmin){
            // AccessDeniedException (not RuntimeException) so the API answers 403 Forbidden,
            // not 400 Bad Request. The request was perfectly well formed - the caller just
            // is not allowed. Using the right status code is what lets a frontend tell
            // "you typed something wrong" apart from "you are not permitted".
            throw new AccessDeniedException("This booking does not belong to you");
        }
    }

    // NEW: the logged-in person's own bookings. Replaces getBookingByUserId(id), which
    // let anyone read anyone else's bookings just by changing the number in the URL.
    public List<Booking> getMyBookings(){
        User user = getLoggedInUser();
        return bookingRepository.findByUserId(user.getId());
    }
}
