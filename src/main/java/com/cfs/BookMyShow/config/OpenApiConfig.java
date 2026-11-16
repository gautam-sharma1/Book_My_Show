package com.cfs.BookMyShow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 SWAGGER SETUP.

 springdoc already finds every endpoint by itself, so this class is not required for
 the docs to appear. It adds two things on top:

  1. A title and description, so the page does not just say "OpenAPI definition".

  2. THE "Authorize" BUTTON. This is the important one. Without it, Swagger UI has no
     way to send your JWT, so every protected endpoint tried from the page comes back
     401 and the documentation looks broken. Declaring the bearer scheme below puts a
     padlock on the page: you paste the token from /api/auth/login once, and Swagger
     attaches "Authorization: Bearer <token>" to every request after that.

 HOW TO USE IT:
   1. open  http://localhost:8080/swagger-ui.html
   2. POST /api/auth/login  with admin@bms.com / admin123
   3. copy the "token" value out of the response
   4. click Authorize (top right), paste the token, click Authorize
   5. every endpoint is now callable as that user
*/
@Configuration
public class OpenApiConfig {

    // The name is just an internal label tying the scheme below to the requirement above.
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI bookMyShowOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("BookMyShow API")
                        .version("1.0")
                        .description("""
                                Movie ticket booking REST API built with Spring Boot, Spring Security (JWT),
                                Spring Data JPA and MySQL.

                                HOW TO USE THIS PAGE
                                1. POST /api/auth/login with admin@bms.com / admin123
                                2. copy the token from the response
                                3. click the Authorize button (top right) and paste it
                                4. you can now call the protected endpoints

                                ROLES
                                - public : browsing movies, shows, cities, theaters, screens, seats
                                - USER   : booking seats, viewing and cancelling your own bookings
                                - ADMIN  : creating/updating/deleting the catalogue, managing users
                                """)
                        .contact(new Contact().name("Gautam Sharma")))

                // Apply the scheme to every operation, so the padlock shows on all of them.
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                // Describe HOW the token travels: an HTTP header, bearer style, JWT format.
                // This is exactly what our JwtAuthFilter reads.
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)   // not apiKey, not oauth2
                                        .scheme("bearer")                 // -> "Authorization: Bearer ..."
                                        .bearerFormat("JWT")              // documentation hint only
                                        .description("Paste the token returned by POST /api/auth/login")));
    }
}
