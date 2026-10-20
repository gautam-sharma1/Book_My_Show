package com.cfs.BookMyShow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
//“Show is nothing but a concept like movie running on screen at a time in difrren room on diffrent screen”
//“Show is a movie running on a specific screen (room) at a specific time.”
@Entity
@Table(name = "shows") // note we not create the dba nd table so  here we are giving the table name  and very important in class the class name start with the pascal case like PlacementCell && in singular form  but in db we have to  pass in small case && in plural form means in db table and  placementcells, City(entity), cities in
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // see in note
public class Show { // here show means show time and in show time movie are played
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate showDate;

    @Column(nullable = false)
    private LocalTime startTime;

    private LocalTime endTime;

    private Double ticketPrice;

    @ManyToOne // as i said show means show time so bhot sare show mae ek hi movie chal sakti  like auditorium1,auditorium2,auditorium3  teeno auditorium mae ek hi movie chal sakte hai
    @JoinColumn(name ="movie_id", nullable = false)
    private Movie movie;

    @ManyToOne // as i said show means show time so bhot sare show mae ek hi screen pe chal sakti  like screen1,screen2,ascreen3  teeno screen mae ek hi show chal sakte hai
    @JoinColumn(name ="screen_id", nullable = false)
    private Screen screen;

}
