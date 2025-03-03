package ru.nsu.usoltsev.worker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"common-test", "local-test"})
@SpringBootTest
class WorkerApplicationTests {

    @Test
    void contextLoads() {
    }

}
