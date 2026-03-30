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
class UnitControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldListUnits() throws Exception {
        mockMvc.perform(get("/api/units")).andExpect(status().isOk()).andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateUnit() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Unit_%s",
              "status": 0
            }
            """.formatted(unique);

        MvcResult result = mockMvc
            .perform(post("/api/units").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Unit_" + unique))
            .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // cleanup
        mockMvc.perform(delete("/api/units/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetUnitById() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Unit_%s",
              "status": 0
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/units").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(get("/api/units/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Unit_" + unique));

        // cleanup
        mockMvc.perform(delete("/api/units/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldPatchUnit() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String createBody = """
            {
              "name": "Unit_%s",
              "status": 0
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/units").contentType(MediaType.APPLICATION_JSON).content(createBody))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");
        String unique2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        mockMvc
            .perform(
                patch("/api/units/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "Unit_%s"}
                        """.formatted(unique2)
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Unit_" + unique2));

        // cleanup
        mockMvc.perform(delete("/api/units/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteUnit() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Unit_%s",
              "status": 0
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/units").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/units/" + id)).andExpect(status().isOk());

        mockMvc.perform(get("/api/units/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetUnitStatusHistory() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Unit_%s",
              "status": 1
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/units").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(get("/api/units/" + id + "/status/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self").exists());

        // cleanup
        mockMvc.perform(delete("/api/units/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetUnitPositionHistory() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Unit_%s",
              "status": 0
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/units").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/units/" + id + "/position/history")).andExpect(status().isOk());

        // cleanup
        mockMvc.perform(delete("/api/units/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentUnit() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/units/" + randomId)).andExpect(status().isNotFound());

        mockMvc
            .perform(patch("/api/units/" + randomId).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/units/" + randomId)).andExpect(status().isNotFound());
    }
}
