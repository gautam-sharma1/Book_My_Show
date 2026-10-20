package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Movie;
import com.cfs.BookMyShow.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Long> {
    List<Movie> findByGenre(String Genre); // note jis chiz ki repository hai vahi chej ham retrun me lenge ya find karenge
   List<Movie> findByLanguage(String Language);
  List<Movie>  findByTitleContainingIgnoreCase(String Title);
}
