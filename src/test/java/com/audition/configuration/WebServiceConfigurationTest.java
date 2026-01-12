package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class WebServiceConfigurationTest {

    @Autowired
    private transient RestTemplate restTemplate;

    @Autowired
    private transient ObjectMapper objectMapper;

    @Test
    void testObjectMapperConfiguration() {
        assertNotNull(objectMapper);
        assertNotNull(objectMapper.getDateFormat());
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testLoggingInterceptorExecution() {
        final MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

        mockServer.expect(requestTo("/test"))
            .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        restTemplate.getForObject("/test", String.class);

        mockServer.verify();
    }
}