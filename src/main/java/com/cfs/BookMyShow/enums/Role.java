package com.cfs.BookMyShow.enums;

// Role = what a logged-in person is allowed to do in the app.
// Exactly the same idea as BookingStatus and SeatType which you already wrote:
// a fixed list of allowed values, stored in the db as the WORD ("USER"/"ADMIN")
// because we put @Enumerated(EnumType.STRING) on the field in User.
//
// Why only 2 values and not a separate "roles" table?
// Big enterprise apps use a many-to-many user_roles table so one user can hold
// several roles at once. Here a person is either a customer or staff, never both,
// so one column is enough. (Good interview line: "enum here, many-to-many at scale".)
public enum Role {
    USER,   // normal customer -> can browse and book tickets
    ADMIN   // staff -> can add/update/delete movies, shows, theaters, screens, seats, cities
}
