package com.cfs.BookMyShow.controller;

import com.cfs.BookMyShow.entity.Movie;
import com.cfs.BookMyShow.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
        @PostMapping("/movie")
        public ResponseEntity<Movie> addMovie(@RequestBody Movie movie){
            return ResponseEntity.status(HttpStatus.CREATED).body(movieService.addMovies(movie));
        }
    @GetMapping("/{movieId}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long movieId){
        return ResponseEntity.ok(movieService.getMovieById(movieId));
    }
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovie(){
        return ResponseEntity.ok(movieService.getallmovies());
    }

    @GetMapping("/language/{Language}")
    public ResponseEntity<List<Movie>>  getMovieByLanguage(@PathVariable String Language){
     return ResponseEntity.ok(movieService.getMovieByLanguage(Language));
    }
    @GetMapping("/search/{MovieBytitleCase}")
    public ResponseEntity<List<Movie>> getMovieBytitleCase(@PathVariable String MovieBytitleCase){
      return ResponseEntity.ok(movieService.getMovieBytitleCase(MovieBytitleCase));
    }
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Movie>> findByGenre(@PathVariable String genre){
        return ResponseEntity.ok(movieService.findByGenre(genre));
    }

    @PutMapping("/{MovieId}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long MovieId, @RequestBody Movie movie){
        return ResponseEntity.ok(movieService.updateMovie(MovieId,movie));
    }
    @DeleteMapping("/{Movieid}")
    public ResponseEntity<String>  deleteMovie( @PathVariable Long Movieid){
        return ResponseEntity.ok(movieService.deleteMovie(Movieid));
    }
}
