package com.cfs.BookMyShow.entity;

import com.cfs.BookMyShow.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
//👉 City → Theater → Screen → Show → Seat -> movie → Booking → User
@Entity
@Table(name = "bookings") // note we not create the dba nd table so  here we are giving the table name  and very important in class the class name start with the pascal case like PlacementCell && in singular form  but in db we have to  pass in small case && in plural form means in db table and  placementcells, City(entity), cities in
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn( name = "show_id" , nullable = false)
    private Show show;

   @ManyToMany // ek booking mae multiple seat book kar sakten hai toh bohot sari booking mae bhot sare seat book kar sakten hai
   @JoinTable(
           name = "booking_seats",
           joinColumns = @JoinColumn(name = "booking_id"), //jahan pe @Jointable likha hoga voh class owning side hogi isislike joincoulumn mase booking_id   the primary key in db and other class is inverse side so that is seat_id the primary key of inverse side class

           inverseJoinColumns = @JoinColumn(name = "seat_id")
   )
    private List<Seat> seats;
    private Double totalPrice;
@Enumerated(EnumType.STRING)
   private BookingStatus status; //instance
    private LocalDateTime bookedAt;
@PrePersist
private void oncreate(){
    if(this.status==null){
        this.status=BookingStatus.CONFIRMED;
    }
}
}//enum default value is what --->null (for instance and static enum variables).
