package main;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        TestRpcServer.class,
},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BehaviorTests {
}
