package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private static final String UNKNOWN_ERROR_MESSAGE = "Upstream API Error";

    @Autowired
    private transient RestTemplate restTemplate;

    public List<AuditionPost> getPosts() {
        try {
            final ResponseEntity<List<AuditionPost>> response = restTemplate.exchange(
                    BASE_URL + "/posts",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (final HttpClientErrorException e) {
            // Wrap upstream errors in SystemException to maintain architectural boundaries
            throw new SystemException("Error fetching posts", UNKNOWN_ERROR_MESSAGE, e.getStatusCode().value(), e);
        }
    }

    public AuditionPost getPostById(final String id) {
        try {
            return restTemplate.getForObject(BASE_URL + "/posts/{id}", AuditionPost.class, id);
        } catch (final HttpClientErrorException e) {
            // Specific handling for 404 to provide a clear error message to the client
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found", 404, e);
            } else {
                throw new SystemException("Error fetching post " + id, UNKNOWN_ERROR_MESSAGE, e.getStatusCode().value(), e);
            }
        }
    }

    public AuditionPost getPostWithComments(final String id) {
        final AuditionPost post = getPostById(id);
        if (post != null) {
            // Composition: Fetch post, then fetch comments and assemble
            post.setComments(getCommentsForPostId(id));
        }
        return post;
    }

    public List<AuditionComment> getCommentsForPostId(final String postId) {
        try {
            final String url = BASE_URL + "/posts/" + postId + "/comments";
            final AuditionComment[] comments = restTemplate.getForObject(url, AuditionComment[].class);
            return comments != null ? Arrays.asList(comments) : Collections.emptyList();
        } catch (final HttpClientErrorException e) {
            throw new SystemException("Error fetching comments for post " + postId, UNKNOWN_ERROR_MESSAGE, e.getStatusCode().value(), e);
        }
    }

    public List<AuditionComment> getCommentsByPostIdQueryParam(final String postId) {
        try {
            final String url = BASE_URL + "/comments?postId=" + postId;
            final AuditionComment[] comments = restTemplate.getForObject(url, AuditionComment[].class);
            return comments != null ? Arrays.asList(comments) : Collections.emptyList();
        } catch (final HttpClientErrorException e) {
            throw new SystemException("Error fetching comments", UNKNOWN_ERROR_MESSAGE, e.getStatusCode().value(), e);
        }
    }
}