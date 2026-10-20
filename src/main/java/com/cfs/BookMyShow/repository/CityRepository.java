package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.City;
import com.cfs.BookMyShow.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City,Long> {

}
