package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowRequest {
   @NotNull(message = "movieId is required")
   private Long movieId;

   @NotNull(message = "screenId is required")
   private Long  screenId;

  // @FutureOrPresent stops anyone creating a show for a date that has already passed.
  @NotNull(message = "showDate is required")
  @FutureOrPresent(message = "showDate cannot be in the past")
  private LocalDate showDate;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    private LocalTime endTime;

    // @Positive = must be greater than zero. Stops free or negative-priced tickets,
    // which would have made totalPrice negative in BookingService.
    @NotNull(message = "ticketPrice is required")
    @Positive(message = "ticketPrice must be greater than 0")
    private Double ticketPrice;
}
