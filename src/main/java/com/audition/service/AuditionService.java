package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    @Autowired
    private transient AuditionIntegrationClient auditionIntegrationClient;

    public List<AuditionPost> getPosts() {
        return auditionIntegrationClient.getPosts();
    }

    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    /**
     * Retrieves a post and enriches it with its associated comments.
     * This demonstrates data composition at the service layer.
     *
     * @param postId The ID of the post
     * @return AuditionPost containing the list of comments
     */
    public AuditionPost getPostWithComments(final String postId) {
        return auditionIntegrationClient.getPostWithComments(postId);
    }

    public List<AuditionComment> getComments(final String postId) {
        return auditionIntegrationClient.getCommentsByPostIdQueryParam(postId);
    }
}