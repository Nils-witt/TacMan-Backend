package dev.nilswitt.webmap.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class MapControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    @WithUserDetails("admin")
    void shouldGetMapRoot() throws UnsupportedEncodingException {
        var result = mockMvcTester.get().uri("/api/map").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$._links.baselayers").isNotNull();
        assertThat(result).bodyJson().extractingPath("$._links.overlays").isNotNull();
        assertThat(result).bodyJson().extractingPath("$._links.items").isNotNull();
    }
}

