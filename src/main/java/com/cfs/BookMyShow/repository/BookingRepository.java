package com.cfs.BookMyShow.repository;

import com.cfs.BookMyShow.entity.Booking;
import com.cfs.BookMyShow.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  BookingRepository extends JpaRepository<Booking,Long> {
List<Booking> findByUserId (Long userId);
 List<Booking> findByShowId(Long showId);
 // find all seat ids who are already booked for given show
 @Query("SELECT s.id from Booking b JOIN b.seats s WHERE b.show.id=:showId AND b.status='CONFIRMED'") //This is JPQL (Java Persistence Query Language)❌ It is NOT SQL❌ It is also not “Java code” exactly
 // JPQL = SQL-like language written using Java objects (entities), not tables  that is why here b.show is not enough show is object so wwe neeed  to get id out of that  so now it is java tern taht is why we have her used Booking  not bokking, Bokking is what we has give naem to class we ahve used seats that iu swhat we made in java  seats so her e we used what we have writtein java class or java term not how things are in db
 List<Long> findBookedSeatIdsByShowId (@Param("showId") Long showId);
//in jpql we use entity instead of  db table which we use in native query
}
//Hibernate internally joins:
//
//booking
//booking_seat (join table)
//seat
//@Query(" SELECT s FROM STUDENT s") // IT RETURN ENTIRE DATA PRESENT IN STUDENT ENTITY
//@Query(" SELECT s FROM STUDENT s where s.id = ?1, ?2") //
//public Student getStudent( Integer studentId,Integer teacherid); // so here the ?1 map to studentId and ?2 map to teacher id that ? vala method is used when there arv multiple argument so ?1 map to first argument then so on
//@Query(" SELECT s FROM STUDENT s where s.id = :studentId" and s.id = :teacherid) //:studentId must match th argument stuednt
//public Student getStudent(Integer studentId,Integer teacherid);

//@Query(" SELECT s FROM STUDENT s where s.id = :studentId" and s.id = :teacherid) //:studentId must match th argument stuednt
//public Student getStudent(@Param("studentId") Integer Id, @Param("teacherid") Integer id); // here we are passing param then no need make he method argument nmae as sma as in @Query written
