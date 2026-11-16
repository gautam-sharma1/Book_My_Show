package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterRequest {

  @NotBlank(message = "theater name is required")
  private String  name ;

    @NotBlank(message = "address is required")
    private String address;

    @NotNull(message = "cityId is required")
    private Long cityId;
}
