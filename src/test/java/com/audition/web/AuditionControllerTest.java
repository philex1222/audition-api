package com.audition.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuditionController.class)
class AuditionControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient AuditionService auditionService;

    @MockBean
    @SuppressWarnings("PMD.UnusedPrivateField")
    private transient AuditionLogger auditionLogger;

    @Test
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.JUnitTestsShouldIncludeAssert"})
    void shouldGetPosts() throws Exception {
        final AuditionPost post = new AuditionPost();
        post.setId(1);
        post.setTitle("Test Title");

        when(auditionService.getPosts()).thenReturn(List.of(post));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"));
    }

    @Test
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.JUnitTestsShouldIncludeAssert"})
    void shouldFilterPosts() throws Exception {
        final AuditionPost post1 = new AuditionPost();
        post1.setTitle("Java");
        final AuditionPost post2 = new AuditionPost();
        post2.setTitle("Python");

        when(auditionService.getPosts()).thenReturn(List.of(post1, post2));

        mockMvc.perform(get("/posts").param("title", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Java"));
    }

    @Test
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.JUnitTestsShouldIncludeAssert"})
    void shouldValidatePostId() throws Exception {
        // Tests that the @Pattern validation on the Controller works
        mockMvc.perform(get("/posts/abc"))
                .andExpect(status().isBadRequest());
    }
}