package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.ShowRequest;
import com.cfs.BookMyShow.entity.Movie;
import com.cfs.BookMyShow.entity.Screen;
import com.cfs.BookMyShow.entity.Show;
import com.cfs.BookMyShow.repository.ShowRepository;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieService movieService;
    private final ScreenService screenService;


    public Show addShow(ShowRequest showRequest){
        Movie movie = movieService.getMovieById(showRequest.getMovieId());
        Screen screen = screenService.getScreenById(showRequest.getScreenId());
        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .showDate(showRequest.getShowDate())
                .endTime(showRequest.getEndTime())
                .ticketPrice(showRequest.getTicketPrice())
                .startTime(showRequest.getStartTime())
                .build();
        return showRepository.save(show);
    }
    public List<Show> getAllShow(){
    return showRepository.findAll();
    }
    public Show getShowById(Long id){
    // OLDER CODE: return showRepository.findById(id).orElseThrow(()-> new RuntimeException(" show not found by id "+id));
    return showRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(" show not found by id "+id)); // NEW: answers 404 Not Found
    }
    // same as getShowById but takes a DB lock on the show row, used only while booking
    public Show getShowByIdForUpdate(Long id){
    // OLDER CODE: return showRepository.findByIdForUpdate(id).orElseThrow(()-> new RuntimeException(" show not found by id "+id));
    return showRepository.findByIdForUpdate(id).orElseThrow(()-> new ResourceNotFoundException(" show not found by id "+id)); // NEW: answers 404 Not Found
    }
    public List<Show>  getShowByMovieId(Long movieId){
return showRepository.findByMovieId(movieId);
    }
    public List<Show> getShowByMovieIdAndDate(Long MovieId , LocalDate date){
        return showRepository.findByMovieIdAndShowDate(MovieId,date);
    }
public List<Show> getShowByScreenId(Long ScreenId){
return showRepository.findByScreenId(ScreenId);
}
public List<Show>  findByScreenIdAndShowDate(Long ScreenId,LocalDate date){
      return showRepository.findByScreenIdAndShowDate(ScreenId,date);
}
}
