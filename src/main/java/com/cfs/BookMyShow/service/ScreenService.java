package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.entity.Screen;
import com.cfs.BookMyShow.repository.ScreenRepository;
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
            throw new RuntimeException("New screen cannot have an ID");
        }// if scrren has id then it not save in db because  because id is auto generated
       return screenRepository.save(screen);
    }
    public List<Screen> getAllScreen(){
       return screenRepository.findAll();
    }
    public Screen getScreenById(Long Id){
       return screenRepository.findById(Id).orElseThrow(()-> new RuntimeException("screen not found by id "+Id));
    }
    public List<Screen> getScreenByTheaterId(Long theaterid){
       return screenRepository.findByTheaterId(theaterid);
    }


}
