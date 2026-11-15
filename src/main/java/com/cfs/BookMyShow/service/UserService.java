package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.AuthResponse;
import com.cfs.BookMyShow.dto.LoginRequest;
import com.cfs.BookMyShow.dto.UserRequest;
import com.cfs.BookMyShow.entity.User;
import com.cfs.BookMyShow.enums.Role;
import com.cfs.BookMyShow.repository.UserRepository;
import com.cfs.BookMyShow.security.JwtUtil;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.exception.DuplicateResourceException;
import com.cfs.BookMyShow.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;// notice something that after putting @RequiredArgsConstructor we not need to make a constructor for private final fields import

    // NEW: BCrypt encoder (the bean we declared in SecurityConfig). Used to scramble
    // passwords on the way in, and to compare a typed password with a stored hash.
    private final PasswordEncoder passwordEncoder;

    // NEW: creates the JWT we hand back after a successful login.
    private final JwtUtil jwtUtil;

    //register
    public User register(UserRequest request ){ // we can do this also the return type of method  is User the entity and the  type of request or parameter the method is accepting is type of UserRequest the DTO because at the end we are returing the entire user object not the the dto kind of user object
        if(userRepository.existsByEmail(request.getEmail())){
                // OLDER CODE: throw new RuntimeException(" user already Exists by email"+request.getEmail());
                throw new DuplicateResourceException(" user already Exists by email"+request.getEmail()); // NEW: answers 409 Conflict
        }

        // OLDER CODE (saved the password exactly as typed, so MySQL stored "secret123"
        // in plain text and anyone with db access could read every user's password).
        // Kept for reference:
        //    User user = User.builder()
        //            .name(request.getName())
        //            .email(request.getEmail())
        //            .password(request.getPassword())
        //            .phone(request.getPhone())
        //            .build();

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    // encode() runs BCrypt: "secret123" becomes "$2a$10$N9qo8uLOickgx2ZMRZ..."
                    // One-way - there is no way back from the hash to the password.
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phone(request.getPhone())
                    // THE ROLE IS HARDCODED HERE ON PURPOSE.
                    // We never read a role from the request. UserRequest does not even
                    // have a role field. That is what stops somebody posting
                    // {"role":"ADMIN"} and making themselves an admin.
                    // Rule to remember: never let the client decide who it is.
                    .role(Role.USER)
                    .build();
    return userRepository.save(user);
    }

    //login

    // OLDER CODE (compared the typed password to the stored one with .equals(), which
    // only worked because passwords were stored in plain text; and it returned the whole
    // User entity instead of a token). Kept for reference:
    //    public User Login(LoginRequest request){
    //     User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("user not found by email"+request.getEmail()));
    //     if(!user.getPassword().equals(request.getPassword())){
    //         throw new RuntimeException("Invalid Password");
    //     }
    //     return user;
    //    }

    // NEW login: checks the password against the BCrypt hash and returns a JWT.
    public AuthResponse Login(LoginRequest request){

        // OLDER CODE: .orElseThrow(()-> new RuntimeException("Invalid email or password"));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password")); // NEW: answers 401 Unauthorized

        // matches(raw, hashed) re-hashes what the user typed using the SAME salt that
        // is stored inside the existing hash, then compares. We can never go the other
        // way (hash -> password), so this is the only way to check a BCrypt password.
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            // OLDER CODE: throw new RuntimeException("Invalid email or password");
            throw new InvalidCredentialsException("Invalid email or password"); // NEW: answers 401 Unauthorized
        }

        // NOTE on the error messages above: both failures say the same vague thing on
        // purpose. If "user not found" and "wrong password" gave different messages, an
        // attacker could use the difference to discover which emails are registered.
        // This is called user enumeration, and it is a nice detail to mention in an interview.

        // Build the token. From here the client sends it on every request as
        // Authorization: Bearer <token>
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // NEW: creates another ADMIN. Only reachable through an admin-protected endpoint,
    // so an existing admin is the only one who can call it.
    public User registerAdmin(UserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            // OLDER CODE: throw new RuntimeException(" user already Exists by email"+request.getEmail());
            throw new DuplicateResourceException(" user already Exists by email"+request.getEmail()); // NEW: answers 409 Conflict
        }
        User admin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ADMIN)   // the difference from register() is this single line
                .build();
        return userRepository.save(admin);
    }

    // NEW: look a user up by email. Needed because once the userId is gone from
    // BookingRequest, the only thing we know about the caller is the email stored
    // inside their token.
    public User getUserByEmail(String email){
        // OLDER CODE: .orElseThrow(() -> new RuntimeException("user not found by email "+email));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user not found by email "+email)); // NEW: answers 404 Not Found
    }

    public List<User> getAllUser(){
        return userRepository.findAll();

    }
    public  User getUserById(Long id){
     // OLDER CODE: .orElseThrow(() -> new RuntimeException("user not found by id"+id));
     return userRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("user not found by id"+id)); // NEW: answers 404 Not Found
    }

 }
