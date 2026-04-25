package dev.nilswitt.tacman.api.map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-test.properties",
    properties = {
        "application.admin.create=true", "application.admin.username=admin", "application.admin.password=admin",
    }
)
class MapControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldGetMapRootWithLinks() throws Exception {
        mockMvc
            .perform(get("/api/map"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.baselayers").exists())
            .andExpect(jsonPath("$._links.overlays").exists())
            .andExpect(jsonPath("$._links.items").exists())
            .andExpect(jsonPath("$._links.groups").exists());
    }
}
