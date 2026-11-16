package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.entity.Screen;
import com.cfs.BookMyShow.repository.ScreenRepository;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenService {
    private final ScreenRepository screenRepository;
    private final TheaterService theaterService;
    public Screen addScreen(Screen screen){
        if(screen.getId() != null){// we write this because  Any entity in your project (like Screen, Theater, Booking, etc.) that has an auto-generated ID can use the same kind of check in its service layer.The database assigns the ID automatically when saving a new record.Ensures that addEntity() really adds, and doesn’t modify existing data.If someone passes an ID, save() might try to update an existing record instead of creating a new one.
            // OLDER CODE: throw new RuntimeException("New screen cannot have an ID");
            throw new InvalidRequestException("New screen cannot have an ID"); // NEW: still 400, clearer exception type
        }// if scrren has id then it not save in db because  because id is auto generated
       return screenRepository.save(screen);
    }
    public List<Screen> getAllScreen(){
       return screenRepository.findAll();
    }
    public Screen getScreenById(Long Id){
       // OLDER CODE: return screenRepository.findById(Id).orElseThrow(()-> new RuntimeException("screen not found by id "+Id));
       return screenRepository.findById(Id).orElseThrow(()-> new ResourceNotFoundException("screen not found by id "+Id)); // NEW: answers 404 Not Found
    }
    public List<Screen> getScreenByTheaterId(Long theaterid){
       return screenRepository.findByTheaterId(theaterid);
    }


}
