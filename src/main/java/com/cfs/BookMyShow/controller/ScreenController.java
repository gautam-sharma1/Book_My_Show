package com.cfs.BookMyShow.controller;

import com.cfs.BookMyShow.entity.Screen;
import com.cfs.BookMyShow.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screen")
@RequiredArgsConstructor
public class ScreenController {
    private final ScreenService screenService;
    @PostMapping("/screen")
    public ResponseEntity<Screen> addScreen(@RequestBody Screen screen){
        return ResponseEntity.ok(screenService.addScreen(screen));
    }
    @GetMapping
    public ResponseEntity<List<Screen>> getAllScreen(){
        return ResponseEntity.ok(screenService.getAllScreen());
    }
    @GetMapping("/{ScreenId}")
    public ResponseEntity<Screen> getScreenById(@PathVariable Long ScreenId){
        return ResponseEntity.ok(screenService.getScreenById(ScreenId));
    }
    @GetMapping("/{theaterid}")
    public ResponseEntity<List<Screen>> getScreenByTheaterId(@PathVariable Long theaterid){
        return ResponseEntity.ok(screenService.getScreenByTheaterId(theaterid));
    }//Ek theater me bahot saare rooms hote hai ✔️
 //👉 Aur har room me sirf 1 screen hoti hai that is why Screen is in List
}
