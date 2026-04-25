package dev.nilswitt.tacman.api;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-test.properties",
    properties = {
        "application.admin.create=true", "application.admin.username=admin", "application.admin.password=admin",
    }
)
class SessionControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldListSessions() throws Exception {
        mockMvc.perform(get("/api/sessions")).andExpect(status().isOk()).andExpect(jsonPath("$._links").exists());
    }

    @Test
    void shouldListSessionsAfterTokenIssuance() throws Exception {
        MvcResult tokenResult = mockMvc
            .perform(
                post("/api/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"username": "admin", "password": "admin"}
                        """)
            )
            .andExpect(status().isOk())
            .andReturn();

        String token = JsonPath.read(tokenResult.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.token");

        mockMvc
            .perform(get("/api/sessions").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links").exists());
    }

    @Test
    void shouldRevokeOwnSession() throws Exception {
        // Issue a token to create a session registration
        MvcResult tokenResult = mockMvc
            .perform(
                post("/api/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"username": "admin", "password": "admin"}
                        """)
            )
            .andExpect(status().isOk())
            .andReturn();

        String token = JsonPath.read(tokenResult.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.token");

        // List sessions using the token — should include the session just created
        MvcResult sessionsResult = mockMvc
            .perform(get("/api/sessions").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn();

        // Extract the tokenId of the first session — use $._embedded.* to navigate regardless
        // of the HATEOAS key name (e.g. "sessionDtoList"). The wildcard returns a list of lists.
        String sessionsJson = sessionsResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        java.util.List<?> embedded = JsonPath.read(sessionsJson, "$._embedded.*");
        java.util.List<?> sessions = (java.util.List<?>) embedded.get(0);
        String tokenId = ((java.util.Map<?, ?>) sessions.get(0)).get("tokenId").toString();

        // Delete the session using the tokenId
        mockMvc
            .perform(delete("/api/sessions/" + tokenId).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
