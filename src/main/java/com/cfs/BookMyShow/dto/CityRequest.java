package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CityRequest {

    @NotBlank(message = "city name is required")
    private String name;

    @NotBlank(message = "state is required")
    private String state;
}
