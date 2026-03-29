package dev.nilswitt.tacman.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "application.admin.create=true",
                "application.admin.username=admin",
                "application.admin.password=admin",
        }
)
class MissionGroupControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldListMissionGroups() throws Exception {
        mockMvc
                .perform(get("/api/missiongroups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateMissionGroup() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String body = """
                {
                  "name": "Mission_%s",
                  "startTime": "2024-01-01T00:00:00Z"
                }
                """.formatted(unique);

        MvcResult result = mockMvc
                .perform(
                        post("/api/missiongroups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Mission_" + unique))
                .andReturn();

        String id = JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.id"
        );

        // cleanup
        mockMvc
                .perform(delete("/api/missiongroups/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetMissionGroupById() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String body = """
                {
                  "name": "Mission_%s",
                  "startTime": "2024-01-01T00:00:00Z"
                }
                """.formatted(unique);

        MvcResult created = mockMvc
                .perform(
                        post("/api/missiongroups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(
                created.getResponse().getContentAsString(),
                "$.id"
        );

        mockMvc
                .perform(get("/api/missiongroups/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Mission_" + unique));

        // cleanup
        mockMvc
                .perform(delete("/api/missiongroups/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateMissionGroup() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String createBody = """
                {
                  "name": "Mission_%s",
                  "startTime": "2024-01-01T00:00:00Z"
                }
                """.formatted(unique);

        MvcResult created = mockMvc
                .perform(
                        post("/api/missiongroups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(
                created.getResponse().getContentAsString(),
                "$.id"
        );

        String updateBody = """
                {
                  "name": "Mission_%s_updated",
                  "startTime": "2024-06-01T00:00:00Z",
                  "unitIds": [],
                  "mapGroupIds": []
                }
                """.formatted(unique);

        mockMvc
                .perform(
                        put("/api/missiongroups/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mission_" + unique + "_updated"));

        // cleanup
        mockMvc
                .perform(delete("/api/missiongroups/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteMissionGroup() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String body = """
                {
                  "name": "Mission_%s",
                  "startTime": "2024-01-01T00:00:00Z"
                }
                """.formatted(unique);

        MvcResult created = mockMvc
                .perform(
                        post("/api/missiongroups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(
                created.getResponse().getContentAsString(),
                "$.id"
        );

        mockMvc
                .perform(delete("/api/missiongroups/" + id))
                .andExpect(status().isOk());

        mockMvc
                .perform(get("/api/missiongroups/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentMissionGroup() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc
                .perform(get("/api/missiongroups/" + randomId))
                .andExpect(status().isNotFound());

        mockMvc
                .perform(
                        put("/api/missiongroups/" + randomId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                                {"name": "x", "startTime": "2024-01-01T00:00:00Z", "unitIds": [], "mapGroupIds": []}
                                                """
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc
                .perform(delete("/api/missiongroups/" + randomId))
                .andExpect(status().isNotFound());
    }
}
