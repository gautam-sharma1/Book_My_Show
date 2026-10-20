package com.cfs.BookMyShow.entity;

import jakarta.persistence.*;
import lombok.*;



                                                                                                             //or aisa bhi like inox ke hi bhot sare thrater hain in whole india and in that all theater of inox therer are one room in that theater and scree present in that theater
                                                                                                       //“Multiple Screens” actually means
                                                                                                       //👉 In a theater:
                                                                                                                  //✔️ Each screen = one separate room (auditorium)
                                                                                                       //✔️ Each room has exactly ONE screen
                                                                                                      //🎥 So:
                                                                                                      //👉 “Multiple screens” =
                                                                                                                    //Multiple rooms inside one theater
@Entity
@Table(name = "screens")                                                                                      // note we not create the dba nd table so  here we are giving the table name  and very important in class the class name start with the pascal case like PlacementCell && in singular form  but in db we have to  pass in small case && in plural form means in db table and  placementcells, City(entity), cities in
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // see in note
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    private Integer totalSeats;

    @ManyToOne // many screen in one theate rmeans one inox present in many places have one screen per place  many screen in that theater in many place but we  are thinking
    @JoinColumn(name = "theater_id",nullable = false)// toh we consoder one theater which is inox present in many place and many screen present inthat on  theater
    private Theater theater;

}
