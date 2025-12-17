package com.audition;

import static org.assertj.core.api.Assertions.assertThat;

import com.audition.web.AuditionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuditionApplicationTests {

    @Autowired
    private transient AuditionController controller;

    /**
     * Sanity check to ensure the Spring Application Context loads successfully.
     * This verifies that all beans (Controllers, Services, Configurations) are wired correctly.
     */
    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
