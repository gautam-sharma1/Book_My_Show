package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
// note mainly when to use DTO :1. when you dont want to send all the fields of entity from client side as request from postman or ui
//2. good practise is never give entity in response  rather then give dto of that entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest { // see in dto of bookingrequestion we not take all the stuff of entity we just take here the the method we have created in  the repository of Booking

   // OLDER CODE (the client told us WHO was booking). Kept for reference:
   // private  Long userId;
   //
   // WHY userId WAS REMOVED - this is the most important security fix in the project.
   // The old request looked like:  {"userId": 5, "showId": 1, "seatIds": [10,11]}
   // and the server simply believed that 5. So any logged-in person could change that
   // number to somebody else's id and book tickets charged to THEIR account.
   //
   // Now the identity comes from the JWT instead: BookingService reads the email out
   // of the token and looks the user up. A token cannot be forged, so nobody can
   // pretend to be someone else.
   //
   // The rule to remember: the client says what it wants to DO; the token says WHO
   // is asking. Never let the client declare its own identity.

   @NotNull(message = "showId is required")
   private  Long showId;

   // @NotEmpty on a List means "the list must exist AND contain at least one item".
   // Without it, booking with "seatIds": [] used to succeed and create an empty
   // booking with a total price of 0.
   @NotEmpty(message = "you must select at least one seat")
   private  List<Long> seatIds;
}
//Include only required fields
//Avoid sensitive data (password, internal IDs)
//Different DTOs for different APIs
//DTO ≠ Entity (don’t copy blindly)
//🎯 Interview Line (important):
// "DTOs are designed based on use-case, not entity structure. We include only necessary fields to optimize performance and improve security."
//A DTO = data you want to send/receive, not your full database structure.