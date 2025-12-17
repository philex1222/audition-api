package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditionServiceTest {

    @Mock
    private transient AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private transient AuditionService auditionService;

    private transient AuditionPost post;
    private transient AuditionComment comment;

    @BeforeEach
    void setUp() {
        post = new AuditionPost();
        post.setId(1);
        post.setUserId(1);
        post.setTitle("Test Title");
        post.setBody("Test Body");

        comment = new AuditionComment();
        comment.setId(1);
        comment.setPostId(1);
        comment.setBody("Comment Body");
    }

    @Test
    void testGetPosts() {
        when(auditionIntegrationClient.getPosts()).thenReturn(List.of(post));

        final List<AuditionPost> result = auditionService.getPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        verify(auditionIntegrationClient).getPosts();
    }

    @Test
    void testGetPostById() {
        when(auditionIntegrationClient.getPostById("1")).thenReturn(post);

        final AuditionPost result = auditionService.getPostById("1");

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(auditionIntegrationClient).getPostById("1");
    }

    @Test
    void testGetPostWithComments() {
        when(auditionIntegrationClient.getPostWithComments("1")).thenReturn(post);

        final AuditionPost result = auditionService.getPostWithComments("1");

        assertNotNull(result);
        verify(auditionIntegrationClient).getPostWithComments("1");
    }

    @Test
    void testGetComments() {
        when(auditionIntegrationClient.getCommentsByPostIdQueryParam("1")).thenReturn(List.of(comment));

        final List<AuditionComment> result = auditionService.getComments("1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditionIntegrationClient).getCommentsByPostIdQueryParam("1");
    }
}