package com.audition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class AuditionIntegrationTest {

    // PMD: Mark fields transient to avoid serialization warnings
    @Autowired
    private transient AuditionService auditionService;

    @Autowired
    private transient RestTemplate restTemplate;

    private transient MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        // Bind the MockServer to the real RestTemplate bean
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldFetchPostsAndDeserializeCorrectly() {
        // Given: A raw JSON response simulating the external API
        final String jsonResponse = """
            [
              {
                "userId": 1,
                "id": 101,
                "title": "Integration Test Title",
                "body": "Integration Test Body"
              }
            ]
            """;

        mockServer.expect(requestTo("https://jsonplaceholder.typicode.com/posts"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        final List<AuditionPost> posts = auditionService.getPosts();

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals(101, posts.get(0).getId());
        assertEquals("Integration Test Title", posts.get(0).getTitle());

        mockServer.verify();
    }

    @Test
    void shouldFetchPostByIdAndDeserializeCorrectly() {
        // Given
        final String jsonResponse = """
            {
              "userId": 1,
              "id": 202,
              "title": "Single Post Title",
              "body": "Single Post Body"
            }
            """;

        mockServer.expect(requestTo("https://jsonplaceholder.typicode.com/posts/202"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        final AuditionPost post = auditionService.getPostById("202");

        // Then
        assertNotNull(post);
        assertEquals(202, post.getId());
        assertEquals("Single Post Title", post.getTitle());

        mockServer.verify();
    }

    @Test
    void shouldHandleUpstream404Error() {
        // Given: The upstream API returns 404
        mockServer.expect(requestTo("https://jsonplaceholder.typicode.com/posts/999"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // When/Then: Our client should wrap this in a SystemException with 404 status
        final SystemException exception = assertThrows(SystemException.class, () -> auditionService.getPostById("999"));

        assertEquals(404, exception.getStatusCode());
        assertEquals("Resource Not Found", exception.getTitle());

        mockServer.verify();
    }
}