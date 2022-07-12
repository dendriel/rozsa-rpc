package main.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import main.StepDefinition;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PostStepDefinition {
    private static final Logger log = LoggerFactory.getLogger(StepDefinition.class);

    private final TestRestTemplate restTemplate;

    public PostStepDefinition(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @When("POST request is made to procedure {string} with payload")
    public void post(String procedure, Map<String, Object> payload) {
        log.info(":::: POST request is made to procedure {} with payload {}", procedure, payload);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        List<Map<String, Object>> args = List.of(payload);
        HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(args, headers);

        StepDefinition.response = restTemplate.postForEntity(
                String.format("/%s", procedure),
                request,
                Void.class
        );

        log.info(":::: GET response status is {}", StepDefinition.response.getStatusCode());
        log.info(":::: GET request body is {}", StepDefinition.response.getBody());
    }

    @And("GET request for {string} should return {int} elements with payload")
    public void getRequestForShouldReturn(String procedure, int results, Map<String, Object> payload) {
        log.info(":::: GET request for {} should return {} elements with payload {}", procedure, results, payload);

        ResponseEntity<? extends ArrayList> response = restTemplate.getForEntity(procedure, new ArrayList<Map<String, Object>>().getClass());

        log.info(":::: GET response status is {}", response.getStatusCode());
        log.info(":::: GET request body is {}", response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(List.of(payload));
    }
}
