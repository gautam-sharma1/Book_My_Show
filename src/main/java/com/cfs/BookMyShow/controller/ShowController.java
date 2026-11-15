package com.cfs.BookMyShow.controller;

import jakarta.validation.Valid;
import com.cfs.BookMyShow.dto.ShowRequest;
import com.cfs.BookMyShow.entity.Show;
import com.cfs.BookMyShow.repository.ShowRepository;
import com.cfs.BookMyShow.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {
private final ShowService showService;

@PostMapping("/show")
public ResponseEntity<Show> addShow(@Valid @RequestBody ShowRequest show){
    return ResponseEntity.ok(showService.addShow(show));
}

@GetMapping
    public ResponseEntity<List<Show>> getAllShows(){
    return ResponseEntity.ok(showService.getAllShow());

}
@GetMapping("/movie/{MovieId}")
   public ResponseEntity<List<Show>> getShowByMovieId(@PathVariable Long MovieId){
    return ResponseEntity.ok(showService.getShowByMovieId(MovieId));
    }
    @GetMapping("/movie/{movieId}/date")//date is for that this api contain date also  and it allowed that after dynamic binfing of movie id we can pass another word also like we have done date
    public ResponseEntity<List<Show>> getShowByMovieIdAndDate(@PathVariable Long movieId ,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){//DateTimeFormat is because to convert date into yyy-mm-dd and LocalDate is for java to understand and datetime formater is because we pass in date in url in form or yyyy-mm-dd  because we are human and we pass in that format and datetimeformat is also because  we haev to save thing in db in yyyy-mm-dd which user can understand nahi to java ,springboot khudse koi bhi format ka date insert kardega
      return ResponseEntity.ok(showService.getShowByMovieIdAndDate(movieId,date));
    }
    @GetMapping("/screen/{screenId}")
   public ResponseEntity<List<Show>> getShowByScreenId(@PathVariable Long screenId){
 return ResponseEntity.ok(showService.getShowByScreenId(screenId));
    }//Screen 1 (one room)
//    It doesn’t run just one show ❌
//    It runs many shows throughout the day
@GetMapping("/screen/{ScreenId}/date")
    public ResponseEntity<List<Show>> findByScreenIdAndShowDate(@PathVariable Long ScreenId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date ){
            return ResponseEntity.ok(showService.findByScreenIdAndShowDate(ScreenId,date));
    }
}
//@RequestParam
//we  need to pass the @RequestParam if we want to pass the thing in the request path variable and request param are difrrent


//@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) tells Spring how to convert a String from the request into a LocalDate object.
//
//        🔹 Meaning in simple words
//
//When you send a request like:
//
//GET /shows/1?date=2026-05-10
//
//Spring receives "2026-05-10" as a String.
//
// This annotation tells Spring:
//
//        “Convert this string into a LocalDate using ISO format (yyyy-MM-dd).”
//
//        🔹 Why it is needed?
//
//Because LocalDate is a Java object, not a string.

//
//Spring must know:
//
//What format the date is in
//How to parse it
//
//Without this, Spring may not understand the format properly and can throw errors.