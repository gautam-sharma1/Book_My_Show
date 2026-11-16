package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Show;
import com.cfs.BookMyShow.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface  ShowRepository extends JpaRepository<Show,Long> {
    List<Show> findByMovieId(Long MovieId);

    // PESSIMISTIC_WRITE = SELECT ... FOR UPDATE. It locks the show row in the DB so that
    // two users booking the SAME show wait for each other instead of running at the same time.
    // This is what stops the same seat from being sold twice.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Show s WHERE s.id = :showId")
    Optional<Show> findByIdForUpdate(@Param("showId") Long showId);
    List<Show> findByScreenIdAndShowDate(Long ScreenId, LocalDate showDate);
    List<Show> findByMovieIdAndShowDate(Long MovieId, LocalDate ShowDate);
    List<Show> findByScreenId(Long ScreenId);

}
