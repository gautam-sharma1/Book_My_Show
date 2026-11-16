package com.cfs.BookMyShow.controller;

import jakarta.validation.Valid;
import com.cfs.BookMyShow.dto.AuthResponse;
import com.cfs.BookMyShow.dto.LoginRequest;
import com.cfs.BookMyShow.dto.UserRequest;
import com.cfs.BookMyShow.entity.User;
import com.cfs.BookMyShow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 THE ONLY TWO PUBLIC DOORS INTO THE APP.

 Register and login used to live in UserController on /api/users/**. They were moved
 here for one practical reason: /api/users/** is now ADMIN-only (it lists every user
 in the system). If login stayed under that prefix, the security rules would have to
 carve exceptions out of their own prefix, which is messy and easy to get wrong.

 Keeping them apart makes SecurityConfig read in plain English:
      /api/auth/**  -> permitAll   (anyone, no token)
      /api/users/** -> hasRole ADMIN

 These two endpoints MUST be public. A locked login endpoint would mean nobody could
 ever get a token, and therefore nobody could ever log in - the chicken and egg problem.
*/
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // POST /api/auth/register
    // Creates a normal customer. The role is forced to USER inside UserService -
    // sending {"role":"ADMIN"} in the body does nothing, because UserRequest has no
    // role field to receive it.
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRequest request){
        // We still return the User entity here, but the password can no longer leak
        // because of the @JsonIgnore we added on User.password.
        return ResponseEntity.ok(userService.register(request));
    }

    // POST /api/auth/login
    // Returns AuthResponse {token, type, email, role} instead of the User object.
    // Copy the token and send it on every later request as:
    //      Authorization: Bearer <token>
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userService.Login(loginRequest));
    }

    // GET /api/auth/me  -> "who am I?"
    //
    // Notice there is NO id in the URL and NO id in the body. Spring hands us an
    // Authentication object which JwtAuthFilter already filled in from the token, and
    // getName() returns the email we signed into it.
    //
    // This is the clearest demonstration of the whole security model: the client cannot
    // ask "give me user 5" - it can only ask "give me ME", and the token decides who
    // that is. Compare it with the old GET /api/booking/user/{userId}, where changing
    // one number in the URL read somebody else's data.
    //
    // SecurityConfig lists /api/auth/me as .authenticated() ABOVE the /api/auth/**
    // permitAll rule, so calling it with no token returns a clean 401 before this
    // method is ever reached. The null check below is just a belt-and-braces guard.
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication){
        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("You must be logged in to view your profile");
        }
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }
}
