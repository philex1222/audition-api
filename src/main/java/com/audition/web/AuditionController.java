package com.audition.web;

import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuditionController {

    @Autowired
    private transient AuditionService auditionService;

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(
            @RequestParam(value = "title", required = false) final String titleFilter,
            @RequestParam(value = "body", required = false) final String bodyFilter) {

        List<AuditionPost> posts = auditionService.getPosts();

        // Filtering logic applied in-memory as the upstream API does not support these specific filters.
        // In a database-backed application, this should be pushed down to the SQL layer.
        if (titleFilter != null && !titleFilter.isEmpty()) {
            posts = posts.stream()
                    .filter(p -> p.getTitle().contains(titleFilter))
                    .collect(Collectors.toList());
        }

        if (bodyFilter != null && !bodyFilter.isEmpty()) {
            posts = posts.stream()
                    .filter(p -> p.getBody().contains(bodyFilter))
                    .collect(Collectors.toList());
        }

        return posts;
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostById(
            // Input Validation: Ensure ID is numeric to prevent malformed requests to upstream
            @PathVariable("id") @Pattern(regexp = "^[0-9]+$", message = "Post ID must be numeric") final String postId,
            @RequestParam(value = "withComments", required = false, defaultValue = "false") final boolean withComments) {

        if (withComments) {
            return auditionService.getPostWithComments(postId);
        }
        return auditionService.getPostById(postId);
    }

    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionComment> getCommentsForPost(
            @PathVariable("id") @Pattern(regexp = "^[0-9]+$", message = "Post ID must be numeric") final String postId) {
        return auditionService.getComments(postId);
    }
}
