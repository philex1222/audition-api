package com.audition.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.audition.common.exception.SystemException;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditionModelTest {

    private static final String TEST_TITLE = "Title";
    private static final String TEST_MESSAGE = "Message";
    private static final String TEST_DETAIL = "Detail";
    private static final String TEST_BODY = "Body";

    @Test
    void testAuditionPost() {
        final AuditionPost post = new AuditionPost();
        post.setId(1);
        post.setUserId(99);
        post.setTitle(TEST_TITLE);
        post.setBody(TEST_BODY);
        post.setComments(List.of(new AuditionComment()));

        assertEquals(1, post.getId());
        assertEquals(99, post.getUserId());
        assertEquals(TEST_TITLE, post.getTitle());
        assertEquals(TEST_BODY, post.getBody());
        assertEquals(1, post.getComments().size());
    }

    @Test
    void testAuditionComment() {
        final AuditionComment comment = new AuditionComment();
        comment.setId(10);
        comment.setPostId(1);
        comment.setName("Name");
        comment.setEmail("email@test.com");
        comment.setBody("Comment Body");

        assertEquals(10, comment.getId());
        assertEquals(1, comment.getPostId());
        assertEquals("Name", comment.getName());
        assertEquals("email@test.com", comment.getEmail());
        assertEquals("Comment Body", comment.getBody());
    }

    @Test
    void testSystemExceptionBasicConstructors() {
        // Test Constructor 1
        final SystemException ex1 = new SystemException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, ex1.getMessage());
        assertEquals("API Error Occurred", ex1.getTitle());

        // Test Constructor 2
        final SystemException ex2 = new SystemException(TEST_MESSAGE, 400);
        assertEquals(400, ex2.getStatusCode());

        // Test Constructor 3
        final RuntimeException cause = new RuntimeException("Cause");
        final SystemException ex3 = new SystemException(TEST_MESSAGE, cause);
        assertEquals(cause, ex3.getCause());
    }

    @Test
    void testSystemExceptionDetailConstructors() {
        final RuntimeException cause = new RuntimeException("Cause");

        // Test Constructor 4
        final SystemException ex4 = new SystemException(TEST_DETAIL, TEST_TITLE, 500);
        assertEquals(TEST_DETAIL, ex4.getDetail());
        assertEquals(TEST_TITLE, ex4.getTitle());

        // Test Constructor 5
        final SystemException ex5 = new SystemException(TEST_DETAIL, TEST_TITLE, cause);
        assertEquals(500, ex5.getStatusCode());

        // Test Constructor 6
        final SystemException ex6 = new SystemException(TEST_DETAIL, 404, cause);
        assertEquals(404, ex6.getStatusCode());

        // Test Constructor 7
        final SystemException ex7 = new SystemException(TEST_DETAIL, TEST_TITLE, 503, cause);
        assertEquals(503, ex7.getStatusCode());
    }
}