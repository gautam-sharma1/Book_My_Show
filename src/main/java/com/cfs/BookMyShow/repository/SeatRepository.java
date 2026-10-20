package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Seat;
import com.cfs.BookMyShow.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByScreenId(Long Screenid); // note jis chiz ki repository hai vahi chej ham retrun me lenge ya find karenge
}
