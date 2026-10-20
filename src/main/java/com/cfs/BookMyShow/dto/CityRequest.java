package com.cfs.BookMyShow.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CityRequest {
    private String name;
    private String state;
}
