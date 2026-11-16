package com.cfs.BookMyShow.entity;

import com.cfs.BookMyShow.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") // note we not create the dba nd table so  here we are giving the table name  and very important in class the class name start with the pascal case like PlacementCell && in singular form  but in db we have to  pass in small case && in plural form means in db table and  placementcells, City(entity), cities in
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // see in note
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false,unique = true)
    private String email;
    // OLDER CODE (password had no @JsonIgnore, so it was sent back to the client in
    // EVERY response - register, login, GET /api/users, and even nested inside a
    // booking as booking.user.password). Kept for reference:
    // @Column(nullable = false)
    // private String password;
    @Column(nullable = false)
    // @JsonIgnore = "never put this field into JSON".
    // Jackson (the library that turns objects into JSON) simply skips it when writing
    // the response. It still gets READ from JSON on the way in, so register/login
    // can still receive a password - it just can never travel back out.
    @JsonIgnore
    private String password;
    private String phone;
    private LocalDateTime createdAt;


    // Every user is either a normal customer (USER) or staff (ADMIN).
    // @Enumerated(EnumType.STRING) -> store the word "USER"/"ADMIN" in the db column,
    // not the number 0/1. Same reason you used it on Seat.seatType and Booking.status:
    // if we stored numbers and later re-ordered the enum, old rows would silently
    // change meaning.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // OLDER CODE (onCreate only set createdAt; there was no role to default). Kept for reference:
    // @PrePersist // see in note the meaning of prepersist
    // protected void onCreate(){
    //     this.createdAt= LocalDateTime.now();
    // }
    @PrePersist // see in note the meaning of prepersist
    protected void onCreate(){
        this.createdAt= LocalDateTime.now();
        // Safety net: if nobody set a role, make them an ordinary USER.
        // Exactly the same trick you used in Booking to default status to CONFIRMED.
        // This means a user can NEVER accidentally be saved with a null role.
        if(this.role == null){
            this.role = Role.USER;
        }
    }
}
