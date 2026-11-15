package com.cfs.BookMyShow.config;

import com.cfs.BookMyShow.security.JwtAccessDeniedHandler;
import com.cfs.BookMyShow.security.JwtAuthEntryPoint;
import com.cfs.BookMyShow.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
 THE RULEBOOK. This single class decides who can call what.

 Remember: the moment spring-boot-starter-security is on the classpath, EVERY
 endpoint is locked by default and Spring shows its own login page. This class
 throws all of that away and installs our own rules instead.

 @EnableWebSecurity    -> switch on Spring Security web support
 @EnableMethodSecurity -> also lets us write @PreAuthorize on individual methods,
                          which we need for checks a URL pattern cannot express
                          (for example "only cancel YOUR OWN booking").
*/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // The six "catalogue" areas only staff may change. Kept in ONE place so a path can
    // never be added to the POST list but forgotten in the DELETE list - which is exactly
    // the kind of copy-paste gap that leaves an endpoint quietly unprotected.
    private static final String[] ADMIN_MANAGED_PATHS = {
            "/api/movies/**", "/api/shows/**", "/api/cities/**",
            "/api/theaters/**", "/api/screen/**", "/api/seats/**"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // ---------------------------------------------------------------
    // 1. PASSWORD ENCODER
    // ---------------------------------------------------------------
    // BCrypt turns "secret123" into something like "$2a$10$N9qo8uLOickgx2ZMRZ..."
    //
    // Two reasons BCrypt is the right choice (good interview material):
    //  a) It is ONE-WAY. There is no "decrypt". You can only hash a new guess and
    //     compare the two hashes. So even someone who steals your whole database
    //     still cannot read anybody's password.
    //  b) It is deliberately SLOW and SALTED. A random salt is baked into every
    //     hash, so two users with the same password get different hashes, and
    //     brute-forcing is expensive. This is exactly why MD5 and SHA-256 must
    //     NOT be used for passwords: they are fast, and fast helps the attacker.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // ---------------------------------------------------------------
    // 2. AUTHENTICATION MANAGER
    // ---------------------------------------------------------------
    // The component that actually performs "check this email + password".
    // We do not build it by hand. We ask Spring for the one it already wired up
    // from our CustomUserDetailsService plus the PasswordEncoder bean above.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ---------------------------------------------------------------
    // 3. CORS
    // ---------------------------------------------------------------
    // CORS is the browser rule that stops a page on one website from calling an
    // API on a different website, unless that API says it is allowed.
    //
    // WHY IS THIS HERE AND NOT IN CorsConfig?
    // Spring Security's filter chain runs BEFORE Spring MVC. A WebMvcConfigurer
    // CORS setup lives inside MVC, so the browser's pre-flight OPTIONS request is
    // rejected with 401 before MVC is ever reached. Declaring CORS as a bean here
    // and wiring it in with http.cors(...) is what makes it actually work.
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        // Which websites are allowed to call this API. "*" is fine while developing.
        // NOTE: if you ever turn on setAllowCredentials(true), "*" becomes illegal
        // and you must list exact origins such as "http://localhost:3000".
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // Without this line the browser hides the Authorization header from JavaScript.
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ---------------------------------------------------------------
    // 4. THE FILTER CHAIN - the actual rules
    // ---------------------------------------------------------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // --- CSRF ---
            // CSRF protection defends against a malicious website tricking your
            // BROWSER into sending a request using cookies the browser attaches
            // automatically. We are stateless and use a token that the browser does
            // NOT attach automatically, so that attack cannot happen and the
            // protection is pointless overhead. That is the correct reason to turn
            // it off for a JWT API - not "it was breaking my POST requests".
            .csrf(csrf -> csrf.disable())

            // Use the CORS bean defined above.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // --- NO SESSIONS ---
            // STATELESS tells Spring: never create an HttpSession, never remember
            // anyone between requests. Every single request must carry its own token.
            // This is the whole point of JWT, and it is what lets you run several
            // copies of the app behind a load balancer with no shared session store.
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // --- OUR OWN 401 / 403 JSON RESPONSES ---
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(jwtAuthEntryPoint)    // 401 = not logged in
                    .accessDeniedHandler(jwtAccessDeniedHandler))   // 403 = wrong role

            // --- WHO CAN CALL WHAT ---
            // Rules are checked TOP TO BOTTOM and the FIRST match wins, so specific
            // rules must be written above general ones.
            .authorizeHttpRequests(auth -> auth

                    // ---------- PUBLIC: no token needed ----------
                    // Login and register MUST be public, otherwise you could never
                    // obtain a token in the first place (chicken and egg problem).
                    // MUST come BEFORE the /api/auth/** permitAll below. Rules are
                    // matched top to bottom and the FIRST match wins, so if permitAll
                    // were first, /api/auth/me would be public and anyone could call it
                    // with no token. Ordering IS the security here.
                    .requestMatchers("/api/auth/me").authenticated()

                    .requestMatchers("/api/auth/**").permitAll()

                    // Swagger's own pages and the JSON it reads must be reachable without
                    // a token, otherwise the documentation page loads blank.
                    // NOTE: in a real production deploy you would lock these down or
                    // disable springdoc entirely - a public API map helps an attacker.
                    .requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/swagger-resources/**").permitAll()

                    // Browsing stays open, exactly like the real BookMyShow site:
                    // you can look at movies and showtimes before making an account.
                    // Note this allows GET only - POST/PUT/DELETE on the same paths
                    // fall through to the ADMIN rules below.
                    .requestMatchers(HttpMethod.GET,
                            "/api/movies/**",
                            "/api/shows/**",
                            "/api/cities/**",
                            "/api/theaters/**",
                            "/api/screen/**",
                            "/api/seats/**").permitAll()

                    // Seeing which seats are still free is part of browsing.
                    .requestMatchers(HttpMethod.GET, "/api/booking/show/*/available-seats").permitAll()

                    // ---------- ADMIN ONLY ----------
                    // Everything that CHANGES the catalogue. Before this existed,
                    // anyone on the internet could call DELETE /api/movies/1.
                    // PATCH is listed even though no @PatchMapping exists today. Without it,
                    // the day somebody adds one it would fall through to .anyRequest()
                    // .authenticated() and ANY logged-in customer could edit the catalogue.
                    // Deny-by-default on the verb costs nothing and closes that trapdoor.
                    .requestMatchers(HttpMethod.POST,   ADMIN_MANAGED_PATHS).hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,    ADMIN_MANAGED_PATHS).hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH,  ADMIN_MANAGED_PATHS).hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, ADMIN_MANAGED_PATHS).hasRole("ADMIN")

                    // The user list, and creating more admins, is staff only.
                    .requestMatchers("/api/users/**").hasRole("ADMIN")

                    // ---------- EVERYTHING ELSE: just needs a login ----------
                    // This covers booking: POST /api/booking, GET /api/booking/my,
                    // and cancel. Any logged-in USER or ADMIN may call these. The
                    // extra "is this actually YOUR booking?" check lives in
                    // BookingService, because a URL pattern cannot express ownership.
                    .anyRequest().authenticated()
            )

            // --- PLUG IN OUR TOKEN READER ---
            // Place JwtAuthFilter BEFORE Spring's username/password filter, so that
            // by the time Spring asks "is this person authenticated?", our filter has
            // already read the token and answered that question.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
