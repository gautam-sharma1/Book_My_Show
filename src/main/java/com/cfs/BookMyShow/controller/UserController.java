package com.cfs.BookMyShow.controller;
import jakarta.validation.Valid;
import com.cfs.BookMyShow.dto.LoginRequest;
import com.cfs.BookMyShow.dto.UserRequest;
import com.cfs.BookMyShow.entity.User;
import com.cfs.BookMyShow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 This whole controller is now ADMIN-ONLY (see SecurityConfig: /api/users/** hasRole ADMIN).

 Reason: it lists every user in the system. Before security existed, anybody could
 call GET /api/users and receive every account - including, back then, the plain-text
 passwords.

 Register and login MOVED OUT of here into AuthController (/api/auth/**), because
 those two must stay public while everything else here is locked down.
*/
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // OLDER CODE (register lived here on /api/users/register and was public).
    // It now lives in AuthController on /api/auth/register, because /api/users/**
    // became ADMIN-only. Kept for reference:
    //    @PostMapping("/register")
    //    public ResponseEntity<User> registeruser(@RequestBody UserRequest request){
    //        return ResponseEntity.ok(userService.register(request));
    //    }

    // OLDER CODE (login lived here and returned the whole User entity, password included).
    // It now lives in AuthController on /api/auth/login and returns AuthResponse with a
    // JWT instead. Kept for reference:
    //    @PostMapping("login")
    //    public ResponseEntity<User> loginUser(@RequestBody LoginRequest loginrequest){
    //        return ResponseEntity.ok(userService.Login(loginrequest));
    //    }

    // NEW: an existing ADMIN creates another ADMIN.
    // This is step 3 of the admin story:
    //   admin #1   -> seeded automatically at startup (AdminSeeder)
    //   admin #2.. -> created here by an admin who is already logged in
    //   normal user-> /api/auth/register, which can only ever produce a USER
    // The URL is already covered by the ADMIN rule in SecurityConfig, so a plain USER
    // calling this gets 403 Forbidden.
    @PostMapping("/admin")
    public ResponseEntity<User> createAdmin(@Valid @RequestBody UserRequest request){
        return ResponseEntity.ok(userService.registerAdmin(request));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        return ResponseEntity.ok(userService.getAllUser());
    }
    @GetMapping("/{id}")
        public ResponseEntity<User> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
        }

}
