package com.cfs.BookMyShow.dto;

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
   private  Long userId;
   private  Long showId;
   private  List<Long> seatIds;
}
//Include only required fields
//Avoid sensitive data (password, internal IDs)
//Different DTOs for different APIs
//DTO ≠ Entity (don’t copy blindly)
//🎯 Interview Line (important):
// "DTOs are designed based on use-case, not entity structure. We include only necessary fields to optimize performance and improve security."
//A DTO = data you want to send/receive, not your full database structure.