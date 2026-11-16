package com.cfs.BookMyShow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/*
 WHAT IS A JWT?
 A JWT (JSON Web Token) is just a long string with 3 parts joined by dots:

      header . payload . signature
      eyJhbGci.eyJzdWIiOiJn.K8sD2f...

 - header    -> which algorithm signed it (HS256 for us)
 - payload   -> the data we put inside: who the user is (subject) + their role + expiry
 - signature -> header+payload scrambled with our SECRET KEY

 IMPORTANT (common interview question): the payload is NOT encrypted. Anyone can
 base64-decode it and read the email and role. What they CANNOT do is change it,
 because changing the payload makes the signature stop matching - and only our
 server knows the secret needed to make a valid signature. So a JWT protects
 against TAMPERING, not against READING. Never put a password inside one.

 WHY USE IT? It makes the server STATELESS. We don't store sessions in memory, so
 the server doesn't have to remember anyone. The token itself carries the proof.
*/
@Component
public class JwtUtil {

    // The secret used to sign and verify. Read from application.properties.
    // ${JWT_SECRET:default} means "use the JWT_SECRET environment variable if it
    // exists, otherwise fall back to the default" - the same pattern you already
    // used for the database password.
    @Value("${app.jwt.secret}")
    private String secret;

    // How long a token stays valid, in milliseconds (we set 24 hours).
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    // Turns our plain-text secret String into the key object JJWT needs.
    // HS256 requires at least 256 bits = 32 characters. If the secret in
    // application.properties is shorter, signing throws at startup - which is
    // actually good: it fails loudly instead of being insecure quietly.
    // A LONGER secret is fine and does no harm; we simply pin the algorithm at the
    // signing call so the key's length never decides it for us.
    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ---------- CREATING A TOKEN (used at login) ----------

    // Builds and signs a token for this user.
    // subject = the email, because that is how we look the user up again later.
    // We also stuff the role in as an extra "claim" so we don't need a db hit
    // on every request just to find out if someone is an ADMIN.
    public String generateToken(String email, String role){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)                 // who this token belongs to
                .claim("role", role)            // extra data we chose to include
                .issuedAt(now)                  // when it was created
                .expiration(expiry)             // when it stops working
                // OLDER CODE (no algorithm argument). Kept for reference:
                // .signWith(getKey())
                //
                // WHY THIS CHANGED: with no algorithm given, JJWT picks the STRONGEST
                // one the key can support. Our secret is 64 characters = 512 bits, so
                // JJWT silently chose HS512 and every token header read {"alg":"HS512"}
                // even though the comments and the plan said HS256. Not insecure - but
                // the code said one thing and did another, which is its own kind of bug.
                // Naming the algorithm makes it explicit and stops the behaviour from
                // changing just because somebody edits the length of the secret.
                .signWith(getKey(), Jwts.SIG.HS256)   // sign it so nobody can edit it
                .compact();                     // turn it into the final string
    }

    // ---------- READING A TOKEN (used on every request) ----------

    // Opens the token, checks the signature AND the expiry, and hands back the payload.
    // If the token was edited, signed with a different secret, or has expired,
    // JJWT throws an exception here - which is exactly what we want.
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())           // check the signature using our secret
                .build()
                .parseSignedClaims(token)       // throws if invalid or expired
                .getPayload();                  // the data part
    }

    // Small helper so the methods below stay one-liners.
    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        return resolver.apply(extractAllClaims(token));
    }

    // Pull the email back out of the token.
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Pull the role back out of the token.
    public String extractRole(String token){
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Is this token still usable?
    // We return true/false instead of letting the exception escape, because a bad
    // token is a normal everyday event (expired, someone poking around), not a crash.
    public boolean isTokenValid(String token){
        try {
            // Expiry is already checked inside parseSignedClaims, but we check again
            // to be explicit and readable.
            return extractAllClaims(token).getExpiration().after(new Date());
        } catch (Exception e){
            return false;   // malformed, wrong signature, or expired
        }
    }
}
