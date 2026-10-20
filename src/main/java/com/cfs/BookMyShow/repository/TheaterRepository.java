package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Theater;
import com.cfs.BookMyShow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheaterRepository extends JpaRepository<Theater,Long> {
    List<Theater> findByCityId(Long Cityid); // note jis chiz ki repository hai vahi chej ham retrun me lenge ya find karenge
}
