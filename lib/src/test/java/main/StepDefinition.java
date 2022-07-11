package main;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class StepDefinition extends IntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(StepDefinition.class);

    private final TestRestTemplate restTemplate;

    ResponseEntity<?> response;

    public StepDefinition(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @When("GET request is made for {string}")
    public void get(String path) {
        log.info(":::: GET request is made for {}", path);

        response = restTemplate.getForEntity(path, Object.class);

        log.info(":::: GET response status is {}", response.getStatusCode());
        log.info(":::: GET request body is {}", response.getBody());
    }

    @Then("response status code is {int}")
    public void validateResponseStatusCode(int status) {
        assertNotNull(response);

        log.info("response status: {} expected: {}", response.getStatusCode().value(), status);

        assertEquals(status, response.getStatusCode().value());
    }

    @And("response has numeric value {double}")
    public void validateNumericResponse(double value) {
        log.info("response status value: {} expected: {}", response.getBody(), value);

        assertNotNull(response);
        assertEquals(value, response.getBody());
    }

    @And("response has boolean value {string}")
    public void validateBooleanResponse(String value) {
        boolean boolValue = Boolean.parseBoolean(value);
        log.info("response status value: {} expected: {}", response.getBody(), boolValue);

        assertNotNull(response);
        assertEquals(boolValue, response.getBody());
    }

    @And("response has text value {string}")
    public void validateTextResponse(String value) {
        log.info("response status value: {} expected: {}", response.getBody(), value);

        assertNotNull(response);
        assertEquals(value, response.getBody());
    }

    @And("response has date value {string}")
    public void validateDateResponse(String value) throws ParseException {
        log.info("response status value: {} expected: {}", response.getBody(), value);

        assertNotNull(response);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        Date expected = dateFormat.parse(value);
        Date received = dateFormat.parse((String) response.getBody());

        assertEquals(expected, received);
    }
}
