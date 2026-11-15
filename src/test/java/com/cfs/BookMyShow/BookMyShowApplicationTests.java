package com.cfs.BookMyShow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// The default smoke test: does the whole Spring application context start at all?
// It catches missing beans, bad annotations and broken configuration before anything else.
//
// The @TestPropertySource was added so the test uses an in-memory H2 database instead
// of MySQL. Without it this test failed on this machine with
// "Access denied for user 'root'@'localhost'", because it was trying to reach a real
// database that is not reachable with the configured password.
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:contexttest;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class BookMyShowApplicationTests {

	@Test
	void contextLoads() {
	}

}
