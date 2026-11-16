package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.TheaterRequest;
import com.cfs.BookMyShow.entity.City;
import com.cfs.BookMyShow.entity.Theater;
import com.cfs.BookMyShow.repository.TheaterRepository;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
//👉 “Services should communicate with other services, not directly with repositories, to maintain separation of concerns.”
public class TheaterService {
    private final TheaterRepository theaterRepository;
//    private final CityRepository cityRepository; // directly cityRepository ka acccess nahi dena hota
    private final CityService cityservice;
    public Theater addTheater(TheaterRequest request){
          City   city = cityservice.getCityById(request.getCityId());

        Theater theater = Theater.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(city)
                .build();
        return theaterRepository.save(theater);
    }
    public List<Theater> getAllTheater(){
      return  theaterRepository.findAll();
    }
    public Theater gettheaterByid(Long id){
        // OLDER CODE: return theaterRepository.findById(id).orElseThrow(()-> new RuntimeException("theater not found by id"+id));
        return theaterRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("theater not found by id"+id)); // NEW: answers 404 Not Found
    }

    public List<Theater> getTheaterByCity(Long cityid){
              return theaterRepository.findByCityId(cityid);

    }

}
