package com.cfs.BookMyShow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 This is a RESPONSE dto - the first one in the project.

 Up to now every dto here (UserRequest, ShowRequest...) was a REQUEST dto: data
 coming IN from the client. This one is the opposite: data going OUT to the client
 after a successful login.

 Why not just return the token as a plain String?
 Because the client needs more than the token to draw the screen. Knowing the role
 is what lets a frontend decide whether to show the "Add Movie" admin button.
 Returning an object also means we can add fields later without breaking anyone.

 Why not return the User entity? Because that would drag the password hash, the
 phone number and createdAt along with it. A dto sends only what is needed.
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    // The JWT itself. The client stores this and sends it back on every later
    // request as the header:  Authorization: Bearer <token>
    private String token;

    // Always "Bearer". It tells the client HOW to send the token back. This is a
    // standard field in OAuth/JWT responses, so including it looks professional.
    private String type;

    // Who logged in - handy for showing "Welcome, g@test.com" without decoding
    // the token on the client side.
    private String email;

    // "USER" or "ADMIN". The frontend uses this to show or hide admin screens.
    //
    // IMPORTANT: this is only for DRAWING THE UI. It is never trusted for security.
    // The server re-checks the real role from the token on every request, so a user
    // editing this value in their browser gains nothing at all.
    private String role;
}
