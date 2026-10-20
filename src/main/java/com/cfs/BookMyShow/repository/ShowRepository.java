package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Show;
import com.cfs.BookMyShow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface  ShowRepository extends JpaRepository<Show,Long> {
    List<Show> findByMovieId(Long MovieId);
    List<Show> findByScreenIdAndShowDate(Long ScreenId, LocalDate showDate);
    List<Show> findByMovieIdAndShowDate(Long MovieId, LocalDate ShowDate);
    List<Show> findByScreenId(Long ScreenId);

}
