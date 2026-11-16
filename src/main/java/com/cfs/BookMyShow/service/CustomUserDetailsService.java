package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.entity.User;
import com.cfs.BookMyShow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 THE BRIDGE BETWEEN YOUR User ENTITY AND SPRING SECURITY.

 Spring Security does not know what a "com.cfs.BookMyShow.entity.User" is. It only
 understands its own interface called UserDetails (username, password, authorities).

 So we implement UserDetailsService - an interface with ONE method - whose job is:
      "given an email, go find that person and describe them in Spring's language."

 Spring calls this automatically whenever it needs to load a user.
*/
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring calls the parameter "username", but OUR username is the email,
    // because that is what users actually log in with.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found by email " + email));

        // Now convert OUR User into Spring's User (note: a different class with the
        // same name, from org.springframework.security.core.userdetails).
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),        // the "username" Spring will remember
                user.getPassword(),     // the BCrypt hash from the db
                // "authorities" = the permissions this person has.
                // VERY IMPORTANT: Spring expects the prefix "ROLE_".
                // We store "ADMIN" in the db, so we hand Spring "ROLE_ADMIN".
                // Later, hasRole("ADMIN") quietly adds the prefix back and compares.
                // Getting this prefix wrong is THE most common Spring Security bug -
                // everything compiles, but every admin request returns 403.
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
