package com.cfs.BookMyShow.security;

import com.cfs.BookMyShow.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 THE GATE. This class runs ONCE for EVERY request that reaches your app,
 before any controller method is called.

 Its only job: look for a token, and if there is a valid one, tell Spring
 "this request belongs to that person". It does NOT decide what they are
 allowed to do - that is SecurityConfig's job.

 WHY "OncePerRequestFilter"? A single HTTP request can bounce around inside
 the server (forwards, error pages). This base class guarantees our code runs
 exactly once per request instead of 2-3 times.

 THE FLOW:
   1. read the "Authorization" header
   2. if it looks like "Bearer <token>", cut off the "Bearer " part
   3. check the token is genuine and not expired
   4. load that user and put them into the SecurityContext
   5. ALWAYS continue the chain - even with no token
*/
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: the token travels in a header that looks like:
        //         Authorization: Bearer eyJhbGciOiJIUzI1NiJ9....
        final String authHeader = request.getHeader("Authorization");

        // No header, or not a Bearer header? Then this is an anonymous request.
        // We do NOT reject it here - maybe they are calling a public endpoint like
        // GET /api/movies. Rejecting is SecurityConfig's decision, not ours.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2: chop off the first 7 characters ("Bearer " is 7 chars) to get the token.
        final String token = authHeader.substring(7);

        try {
            // Step 3: is the signature genuine and the token still in date?
            if (jwtUtil.isTokenValid(token)) {

                String email = jwtUtil.extractEmail(token);

                // Only set it if nobody is authenticated yet. Prevents us from
                // overwriting an authentication some other filter already established.
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Step 4: load the real user so we get their CURRENT role from the db.
                    // (We could read the role straight out of the token, but going to the
                    // db means a user demoted from ADMIN loses access immediately instead
                    // of staying admin until their token expires.)
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // This object is Spring's way of saying "this request is authenticated".
                    // The password slot is null on purpose - the token already proved
                    // identity, so there is nothing left to check.
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()   // the ROLE_USER / ROLE_ADMIN list
                            );

                    // Attach request details (IP address, session id) - standard practice.
                    auth.setDetails(new org.springframework.security.web.authentication
                            .WebAuthenticationDetailsSource().buildDetails(request));

                    // Put it in the SecurityContext. THIS is the line that actually logs
                    // the user in for this one request. From here on, anything in the app
                    // can ask "who is calling?" and get an answer.
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            // A broken or expired token must never crash the app. We simply leave the
            // request anonymous; SecurityConfig will then answer 401 if the endpoint
            // needed a login.
            SecurityContextHolder.clearContext();
        }

        // Step 5: hand the request on to the next filter (and eventually the controller).
        // Forgetting this line makes every request hang forever - a classic mistake.
        filterChain.doFilter(request, response);
    }
}
