package main;

import org.springframework.boot.test.web.client.TestRestTemplate;

public class RestClientHelper {
    private final TestRestTemplate restTemplate;

    public RestClientHelper(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
