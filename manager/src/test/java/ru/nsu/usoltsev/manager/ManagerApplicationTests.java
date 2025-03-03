package ru.nsu.usoltsev.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"common-test", "local-test"})
class ManagerApplicationTests {

    @Test
    void contextLoads() {
    }

}
