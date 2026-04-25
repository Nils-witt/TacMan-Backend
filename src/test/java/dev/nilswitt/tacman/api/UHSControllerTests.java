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
class UHSControllerTests {

    @Autowired
    MockMvc mockMvc;

    private String createBody(String name) {
        return """
            {
              "name": "%s",
              "location": {
                "latitude": 48.0,
                "longitude": 11.0,
                "altitude": 0.0,
                "accuracy": 0.0
              },
              "capacity": 10,
              "assignedPersonellIds": [],
              "missionId": null
            }
            """.formatted(name);
    }

    @Test
    @WithUserDetails("admin")
    void shouldListUHS() throws Exception {
        mockMvc.perform(get("/api/uhs")).andExpect(status().isOk()).andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateUHS() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult result = mockMvc
            .perform(post("/api/uhs").contentType(MediaType.APPLICATION_JSON).content(createBody("UHS_" + unique)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("UHS_" + unique))
            .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        mockMvc.perform(delete("/api/uhs/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetUHSById() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult created = mockMvc
            .perform(post("/api/uhs").contentType(MediaType.APPLICATION_JSON).content(createBody("UHS_" + unique)))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(get("/api/uhs/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("UHS_" + unique));

        mockMvc.perform(delete("/api/uhs/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateUHS() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult created = mockMvc
            .perform(post("/api/uhs").contentType(MediaType.APPLICATION_JSON).content(createBody("UHS_" + unique)))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        String unique2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        mockMvc
            .perform(
                put("/api/uhs/" + id).contentType(MediaType.APPLICATION_JSON).content(createBody("UHS_" + unique2))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UHS_" + unique2));

        mockMvc.perform(delete("/api/uhs/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteUHS() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult created = mockMvc
            .perform(post("/api/uhs").contentType(MediaType.APPLICATION_JSON).content(createBody("UHS_" + unique)))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/uhs/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/uhs/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentUHS() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/uhs/" + randomId)).andExpect(status().isNotFound());
        mockMvc
            .perform(put("/api/uhs/" + randomId).contentType(MediaType.APPLICATION_JSON).content(createBody("x")))
            .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/uhs/" + randomId)).andExpect(status().isNotFound());
    }
}
