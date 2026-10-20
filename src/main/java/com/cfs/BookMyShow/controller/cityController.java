package com.cfs.BookMyShow.controller;

import com.cfs.BookMyShow.dto.CityRequest;
import com.cfs.BookMyShow.entity.City;
import com.cfs.BookMyShow.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class cityController {
    private final CityService cityService;
    @PostMapping("/add")
    public ResponseEntity<City> addcity(@RequestBody CityRequest cityRequest){
        return ResponseEntity.ok(cityService.addCity(cityRequest));
    }
    @GetMapping
    public ResponseEntity<List<CityRequest>> getAllCities(){
return ResponseEntity.ok(cityService.getAllCities());
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<City> getCityById( @PathVariable Long cityId){
        return ResponseEntity.ok(cityService.getCityById(cityId));
    }
}
