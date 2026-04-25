package dev.nilswitt.tacman.api;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
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
class PermissionControllerTests {

    @Autowired
    MockMvc mockMvc;

    private MvcResult createMissionGroup(String unique) throws Exception {
        return mockMvc
            .perform(
                post("/api/missiongroups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "Mission_%s", "startTime": "2024-01-01T00:00:00Z", "unitIds": [], "mapGroupIds": []}
                        """.formatted(unique)
                    )
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetPermissionsForEntity() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String missionId = JsonPath.read(createMissionGroup(unique).getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(get("/api/permissions/missiongroup/" + missionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.users").isArray())
            .andExpect(jsonPath("$.groups").isArray());

        mockMvc.perform(delete("/api/missiongroups/" + missionId)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGrantAndRevokeUserPermission() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String missionId = JsonPath.read(createMissionGroup(unique).getResponse().getContentAsString(), "$.id");

        MvcResult userResult = mockMvc
            .perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"username": "u_%s", "email": "%s@test.com", "firstName": "F", "lastName": "L", "enabled": true, "locked": false}
                        """.formatted(unique, unique)
                    )
            )
            .andExpect(status().isOk())
            .andReturn();

        String userId = JsonPath.read(userResult.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(
                post("/api/permissions/missiongroup/" + missionId + "/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"subjectId": "%s", "scope": "VIEW"}
                        """.formatted(userId))
            )
            .andExpect(status().isOk());

        mockMvc
            .perform(get("/api/permissions/missiongroup/" + missionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.users[0].userId").value(userId));

        mockMvc
            .perform(delete("/api/permissions/missiongroup/" + missionId + "/users/" + userId))
            .andExpect(status().isOk());

        // cleanup
        mockMvc.perform(delete("/api/missiongroups/" + missionId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/users/" + userId)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGrantAndRevokeGroupPermission() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String missionId = JsonPath.read(createMissionGroup(unique).getResponse().getContentAsString(), "$.id");

        MvcResult groupResult = mockMvc
            .perform(
                post("/api/securitygroups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name": "Group_%s", "ssoGroupName": "", "roles": []}
                        """.formatted(unique))
            )
            .andExpect(status().isOk())
            .andReturn();

        String groupId = JsonPath.read(groupResult.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(
                post("/api/permissions/missiongroup/" + missionId + "/groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"subjectId": "%s", "scope": "VIEW"}
                        """.formatted(groupId))
            )
            .andExpect(status().isOk());

        mockMvc
            .perform(delete("/api/permissions/missiongroup/" + missionId + "/groups/" + groupId))
            .andExpect(status().isOk());

        // cleanup
        mockMvc.perform(delete("/api/missiongroups/" + missionId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/securitygroups/" + groupId)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForUnknownEntity() throws Exception {
        mockMvc.perform(get("/api/permissions/missiongroup/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn400ForUnknownEntityType() throws Exception {
        mockMvc.perform(get("/api/permissions/unknowntype/" + UUID.randomUUID())).andExpect(status().isBadRequest());
    }
}
