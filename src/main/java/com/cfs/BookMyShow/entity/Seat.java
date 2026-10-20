package com.cfs.BookMyShow.entity;

import com.cfs.BookMyShow.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats") // note we not create the dba nd table so  here we are giving the table name  and very important in class the class name start with the pascal case like PlacementCell && in singular form  but in db we have to  pass in small case && in plural form means in db table and  placementcells, City(entity), cities in
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // see in note
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber; //like A-34 ,h-76
    @Column(name = "seat_row")
    private String row;// A G K LIKE STRING Note:- row is reserved keyword in mysql that why @column otherwise throw an error
    @Column(name = "seat_col")
    private String col; // like 1 2 4 6
 // specifically jab ham springboot application mae enum use karte hai toh hame batana padta hai ki ham enum use kar rahe hai because in db  there is no term like enum
    @Enumerated(EnumType.STRING)// in  db there is no term like enum so we are saying that  jo enum
    private SeatType seatType;// seat ka type runtime me springboot put kardega like regular and etc but we ahve to store in to db and in db there is no term like enum  so to store into db we say type is EnumTtype.String
   @ManyToOne
   @JoinColumn(name="screen_id",nullable = false)
    private Screen screen;

}
