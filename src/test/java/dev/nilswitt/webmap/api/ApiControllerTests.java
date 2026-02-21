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
class ApiControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    @WithUserDetails("admin")
    void shouldGetApiRoot() throws UnsupportedEncodingException {
        var result = mockMvcTester.get().uri("/api").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$._links.users").isNotNull();
        assertThat(result).bodyJson().extractingPath("$._links.units").isNotNull();
        assertThat(result).bodyJson().extractingPath("$._links.map").isNotNull();
    }
}

