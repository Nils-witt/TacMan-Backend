package dev.nilswitt.tacman.api.map;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-test.properties",
    properties = {
        "application.admin.create=true",
        "application.admin.username=admin",
        "application.admin.password=admin"
    }
)
class MapGroupControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldListMapGroups() throws Exception {
        mockMvc.perform(get("/api/map/groups"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateMapGroup() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Group_%s"
            }
            """.formatted(unique);

        MvcResult result = mockMvc.perform(post("/api/map/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Group_" + unique))
            .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // cleanup
        mockMvc.perform(delete("/api/map/groups/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetMapGroupById() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Group_%s"
            }
            """.formatted(unique);

        MvcResult created = mockMvc.perform(post("/api/map/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/map/groups/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Group_" + unique));

        // cleanup
        mockMvc.perform(delete("/api/map/groups/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateMapGroup() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String createBody = """
            {
              "name": "Group_%s"
            }
            """.formatted(unique);

        MvcResult created = mockMvc.perform(post("/api/map/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        String updateBody = """
            {
              "name": "Group_%s_updated"
            }
            """.formatted(unique);

        mockMvc.perform(put("/api/map/groups/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Group_" + unique + "_updated"));

        // cleanup
        mockMvc.perform(delete("/api/map/groups/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteMapGroup() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Group_%s"
            }
            """.formatted(unique);

        MvcResult created = mockMvc.perform(post("/api/map/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/map/groups/" + id))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/map/groups/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentMapGroup() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/map/groups/" + randomId))
            .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/map/groups/" + randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"x\"}"))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/map/groups/" + randomId))
            .andExpect(status().isNotFound());
    }
}
