package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.entity.Movie;
import com.cfs.BookMyShow.repository.MovieRepository;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    public Movie addMovies(Movie movie){
       return movieRepository.save(movie);

    }
    public List<Movie> getallmovies(){
        return movieRepository.findAll();
    }
    public Movie getMovieById(Long Id){
       // OLDER CODE: return movieRepository.findById(Id).orElseThrow(()-> new RuntimeException("movie not found by id"+Id));
       return movieRepository.findById(Id).orElseThrow(()-> new ResourceNotFoundException("movie not found by id"+Id)); // NEW: answers 404 Not Found

    }

    public List<Movie> getMovieByLanguage(String Language){
       return movieRepository.findByLanguage(Language);
    }
    public  List<Movie> getMovieBytitleCase(String TitleCasecontainingIgnoreCase){
       return movieRepository.findByTitleContainingIgnoreCase(TitleCasecontainingIgnoreCase);
    }
    public List<Movie> findByGenre(String Genre){
        return movieRepository.findByGenre(Genre);
    }
    public Movie updateMovie(Long MovieId,Movie movie){
       // OLDER CODE: Movie ExistingMovie= movieRepository.findById(MovieId).orElseThrow(()->new RuntimeException("movie not found by id"+MovieId));
       Movie ExistingMovie= movieRepository.findById(MovieId).orElseThrow(()->new ResourceNotFoundException("movie not found by id"+MovieId)); // NEW: answers 404 Not Found
        ExistingMovie.setGenre(movie.getGenre());
        ExistingMovie.setDescription(movie.getDescription());
        ExistingMovie.setLanguage(movie.getLanguage());
        ExistingMovie.setRating(movie.getRating());
        ExistingMovie.setTitle(movie.getTitle());
        ExistingMovie.setDurationMinutes(movie.getDurationMinutes());
        ExistingMovie.setPosterUrl(movie.getPosterUrl());
        ExistingMovie.setReleaseDate(movie.getReleaseDate());
       return movieRepository.save(ExistingMovie);

    }

    public String deleteMovie(Long MovieId ){
        // OLDER CODE: Movie movie= movieRepository.findById(MovieId).orElseThrow(() -> new RuntimeException("movie not found by id " + MovieId));
        Movie movie= movieRepository.findById(MovieId).orElseThrow(() -> new ResourceNotFoundException("movie not found by id " + MovieId)); // NEW: answers 404 Not Found
         movieRepository.delete(movie);
         return "deleted movie Successfully";

    }
}
