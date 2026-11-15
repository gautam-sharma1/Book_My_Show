package com.cfs.BookMyShow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 SECURITY TESTS - proving the rules actually work.

 These are the tests almost no student project has, and they are cheap to write.
 Each one locks in one rule, so if someone later edits SecurityConfig and breaks it,
 the build fails instead of the bug reaching an interview demo.

 @SpringBootTest       -> start the REAL application (real filters, real security)
 @AutoConfigureMockMvc -> give us MockMvc, which fires fake HTTP requests at the app
                          without needing a real port or a real browser
 @TestPropertySource   -> swap MySQL for an in-memory H2 database, so the tests run
                          anywhere with no database set up
*/
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:securitytest;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        // Pin the seeded admin for the test instead of relying on whatever the real
        // application.properties happens to hold. Without this, setting an ADMIN_PASSWORD
        // environment variable on any machine would break the build - a test must never
        // depend on configuration that can change outside it.
        "app.admin.email=testadmin@bms.com",
        "app.admin.password=testadmin123",
        "app.admin.name=Test Admin"
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Helper: log in and pull the token out of the AuthResponse.
    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    // Helper: create a normal USER account.
    private void registerUser(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"" + email + "\",\"password\":\""
                                + password + "\",\"phone\":\"9999999999\"}"))
                .andExpect(status().isOk());
    }

    // ---------------------------------------------------------------
    // TEST 1: browsing stays public
    // ---------------------------------------------------------------
    // Proves adding Spring Security did not accidentally lock the whole app.
    // This is the test that catches the classic "everything returns 401 now" mistake.
    @Test
    @DisplayName("public endpoint works with NO token")
    void publicEndpointIsOpen() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk());
    }

    // ---------------------------------------------------------------
    // TEST 2: protected endpoint rejects anonymous callers
    // ---------------------------------------------------------------
    // 401 = "I do not know who you are". Booking must never be anonymous.
    @Test
    @DisplayName("protected endpoint returns 401 without a token")
    void protectedEndpointRejectsAnonymous() throws Exception {
        mockMvc.perform(post("/api/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"showId\":1,\"seatIds\":[1]}"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------------------------------------------
    // TEST 3: a normal USER cannot do admin things
    // ---------------------------------------------------------------
    // 403 = "I know who you are, and you are not allowed". This is the test that
    // proves role separation actually works - before security, ANY stranger could
    // delete a movie.
    @Test
    @DisplayName("USER token gets 403 on an ADMIN-only endpoint")
    void userCannotCallAdminEndpoint() throws Exception {
        registerUser("user1@test.com", "password123");
        String userToken = loginAndGetToken("user1@test.com", "password123");

        mockMvc.perform(post("/api/movies/movie")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Hacked\",\"genre\":\"X\"}"))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------
    // TEST 4: the seeded ADMIN can do admin things
    // ---------------------------------------------------------------
    // Two things at once: the startup seeder really created an admin, and the
    // ROLE_ prefix in CustomUserDetailsService is correct. If that prefix were
    // wrong this test would fail with 403 while everything still compiled.
    @Test
    @DisplayName("ADMIN token succeeds on an ADMIN-only endpoint")
    void adminCanCallAdminEndpoint() throws Exception {
        String adminToken = loginAndGetToken("testadmin@bms.com", "testadmin123");

        mockMvc.perform(post("/api/movies/movie")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Inception\",\"genre\":\"SciFi\",\"language\":\"English\"}"))
                .andExpect(status().isCreated());
    }

    // ---------------------------------------------------------------
    // TEST 5: the password never leaves the server, and login returns a real token
    // ---------------------------------------------------------------
    // Guards the @JsonIgnore on User.password. If someone removes that annotation,
    // this test fails immediately instead of the leak going unnoticed.
    @Test
    @DisplayName("password is never in a response, and login returns a JWT")
    void passwordNeverLeaksAndTokenLooksValid() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Leak\",\"email\":\"leak@test.com\","
                                + "\"password\":\"supersecret\",\"phone\":\"9999999999\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = registerResult.getResponse().getContentAsString();
        assertThat(body).doesNotContain("supersecret");   // the raw password
        assertThat(body).doesNotContain("password");      // the field itself

        // And the token should be a real JWT: three parts separated by dots.
        String token = loginAndGetToken("leak@test.com", "supersecret");
        assertThat(token.split("\\.")).hasSize(3);
    }

    // ---------------------------------------------------------------
    // TEST 6: you cannot sign up as an admin
    // ---------------------------------------------------------------
    // The privilege-escalation check. Even sending "role":"ADMIN" in the body must
    // produce an ordinary USER, because UserRequest has no role field and
    // UserService hardcodes Role.USER.
    @Test
    @DisplayName("sending role ADMIN at registration does NOT make you an admin")
    void cannotSelfPromoteToAdmin() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sneaky\",\"email\":\"sneaky@test.com\","
                                + "\"password\":\"password123\",\"phone\":\"9999999999\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isOk());

        String token = loginAndGetToken("sneaky@test.com", "password123");

        // If the self-promotion had worked, this would return 201 Created.
        mockMvc.perform(post("/api/movies/movie")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"ShouldNotWork\"}"))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------
    // TEST 7: bean validation actually runs
    // ---------------------------------------------------------------
    // Guards the @Valid annotations. The classic trap is that @NotBlank/@Email compile
    // fine without spring-boot-starter-validation on the classpath, and are then
    // silently ignored at runtime - the API "validates" while accepting anything.
    // This test fails loudly if that ever happens.
    @Test
    @DisplayName("invalid registration is rejected with 400 and per-field messages")
    void validationRejectsBadInput() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))                       // nothing supplied at all
                .andExpect(status().isBadRequest());

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"email\":\"notanemail\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        // The response should name each offending field, so a frontend can show the
        // message under the right input box.
        assertThat(body).contains("email");
        assertThat(body).contains("password");
        assertThat(body).contains("name");
    }
}
