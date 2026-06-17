package com.school.school_app;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires live MongoDB Atlas connection — run manually against the real DB")
class SchoolAppApplicationTests {

    @Test
    void contextLoads() {
    }

}
