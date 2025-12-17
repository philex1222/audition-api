package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AuditionIntegrationClientTest {

    @Mock
    private transient RestTemplate restTemplate;

    @InjectMocks
    private transient AuditionIntegrationClient client;

    @Test
    void shouldGetPostsSuccess() {
        final AuditionPost post = new AuditionPost();
        post.setId(1);
        final ResponseEntity<List<AuditionPost>> response = ResponseEntity.ok(List.of(post));

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                ArgumentMatchers.<ParameterizedTypeReference<List<AuditionPost>>>any())
        ).thenReturn(response);

        final List<AuditionPost> result = client.getPosts();
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void shouldGetPostByIdSuccess() {
        final AuditionPost post = new AuditionPost();
        post.setId(1);
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class), anyString())).thenReturn(post);

        final AuditionPost result = client.getPostById("1");
        assertEquals(1, result.getId());
    }

    @Test
    void shouldGetPostByIdNotFound() {
        // Simulates a 404 from upstream to ensure our client wraps it in a SystemException with 404 status
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        final SystemException exception = assertThrows(SystemException.class, () -> client.getPostById("999"));
        assertEquals(404, exception.getStatusCode());
        assertEquals("Resource Not Found", exception.getTitle());
    }

    @Test
    void shouldGetPostByIdServerError() {
        // Simulates a 500 from upstream to ensure our client defaults to 500
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        final SystemException exception = assertThrows(SystemException.class, () -> client.getPostById("1"));
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void shouldGetCommentsForPostIdSuccess() {
        final AuditionComment comment = new AuditionComment();
        final AuditionComment[] comments = {comment};

        when(restTemplate.getForObject(anyString(), eq(AuditionComment[].class))).thenReturn(comments);

        final List<AuditionComment> result = client.getCommentsForPostId("1");
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetPostWithCommentsSuccess() {
        final AuditionPost post = new AuditionPost();
        post.setId(1);

        final AuditionComment comment = new AuditionComment();
        final AuditionComment[] comments = {comment};

        // Mocking sequential calls to build the composite object
        when(restTemplate.getForObject(eq("https://jsonplaceholder.typicode.com/posts/{id}"), eq(AuditionPost.class), eq("1")))
                .thenReturn(post);

        when(restTemplate.getForObject(eq("https://jsonplaceholder.typicode.com/posts/1/comments"), eq(AuditionComment[].class)))
                .thenReturn(comments);

        final AuditionPost result = client.getPostWithComments("1");

        assertEquals(1, result.getId());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
    }
}