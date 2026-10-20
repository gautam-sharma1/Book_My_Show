package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.CityRequest;
import com.cfs.BookMyShow.entity.City;
import com.cfs.BookMyShow.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService { // Note always when using @RequiredArgsConstructor then for field injection happen by private final only if write private  CityRepository cityRepository; (without final then it not injected in that class where it is written ) if using autowired then no need to write final
    private final CityRepository cityRepository;

    public City addCity(CityRequest cityRequest){
        City city = City.builder() // note if passing dto in request or method parameter like here we have done so to save that into the database we have  convert that dto request into entity then it will save into the db that is why we have do  that all builder stuff to create the obj of city from the requestcity we get from dto
                .name(cityRequest.getName())
                .state(cityRequest.getState())
                .build();
        // we can do that builder stuff by this also
//        private City addCity(CityRequest cityRequest){
//            City city = new City();
//
//            city.setName(cityRequest.getName());
//            city.setState(cityRequest.getState());
//
//            return cityRepository.save(city);
//        }
//        and by this also
//        private City addCity(CityRequest cityRequest){
//            City city = new City(
//                    cityRequest.getName(),
//                    cityRequest.getState()
//            );
//
//            return cityRepository.save(city);
//        }
        return cityRepository.save(city);
    }
     public List<CityRequest> getAllCities(){
         List<City> cities = cityRepository.findAll();
         List<CityRequest> responseList = new ArrayList<>();
         for(City city:cities){
    CityRequest cityRequest = new CityRequest();
    cityRequest.setName(city.getName());
    cityRequest.setState(city.getState());
    responseList.add(cityRequest);
    // with builder also we can do ths public List<CityResponse> getAllCities(){
             //    List<City> cities = cityRepository.findAll();
             //    List<CityResponse> responseList = new ArrayList<>();
             //    for (City city : cities) {
             //        CityResponse response = CityResponse.builder()
             //                .name(city.getName())
             //                .state(city.getState())
             //                .build();
             //        responseList.add(response);
             //    }
             //    return responseList;
             //}
         }
         return responseList;
     }


    // public CityRequest getCityById(Long id){
    public City getCityById(Long id){
        City city = cityRepository.findById(id).orElseThrow(()-> new RuntimeException("city not found by id"+id));
           //if not using builder
        // CityRequest cityreq = new CityRequest();
         //cityreq.setState(city.getState());
         //cityreq.setName(city.getName());
         //return cityreq;
        return city;
         //if using builder then
//         return CityRequest.builder().name(city.getName()).state(city.getState()).build();
     }

}
//DTO (CityRequest) → used for INPUT (POST/PUT) for taking input request dto is creaed
//
//Entity (City) → used inside DB layer
//and create one more CityResponse for responding to client  ,for responding cityresponce  a one more dto is created  which is used fro responding likr findall
//but for current as self project we not created cityResponse for responding