package com.cfs.BookMyShow.config;

import com.cfs.BookMyShow.entity.User;
import com.cfs.BookMyShow.enums.Role;
import com.cfs.BookMyShow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 THE CHICKEN AND EGG PROBLEM.

 Admin endpoints can only be called by an ADMIN. But on a brand new database there
 is no admin, and /api/auth/register can only ever create a USER (on purpose - see
 UserService). So no admin could ever come into existence through the API.

 The answer: create the very first admin OUTSIDE the API, right after the app boots.
 After that, admin #1 can create admin #2, #3... through POST /api/users/admin.

 CommandLineRunner is simply Spring saying "run this code once, immediately after
 startup is finished".

 WHY NOT the two obvious alternatives?
  - Letting /register accept a role: anyone could post {"role":"ADMIN"} and promote
    themselves. That is a textbook privilege escalation bug.
  - Running an UPDATE in MySQL by hand: then the project cannot be demoed from a
    clean database, which is exactly what happens when an interviewer clones it.
*/
@Configuration
public class AdminSeeder {

    // Credentials come from application.properties, not hardcoded here, so they can
    // be changed per machine via environment variables. Same ${VAR:default} pattern
    // you already used for the database password.
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder){
        return args -> {

            // THE GUARD. Without this check a duplicate admin would be attempted on
            // every single restart, and the unique constraint on email would blow up
            // the whole application at boot.
            if (userRepository.existsByEmail(adminEmail)) {
                return;   // admin already exists -> nothing to do
            }

            User admin = User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    // Hash it, exactly like a normal signup would. Never store the
                    // raw password, not even for the admin.
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)     // the one place in the whole app that sets ADMIN directly
                    .build();

            userRepository.save(admin);

            // A console line so you can see it happened on first run.
            System.out.println(">>> First ADMIN created: " + adminEmail);
        };
    }
}
