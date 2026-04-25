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
class PatientControllerTests {

    @Autowired
    MockMvc mockMvc;

    private String createBody(String suffix) {
        return """
            {
              "firstName": "John_%s",
              "lastName": "Doe_%s",
              "birthdate": "1990-01-01",
              "street": "Main St",
              "housenumber": "1",
              "postalcode": "12345",
              "city": "Testville",
              "gender": "male",
              "supervising1": null,
              "supervising2": null
            }
            """.formatted(suffix, suffix);
    }

    @Test
    @WithUserDetails("admin")
    void shouldListPatients() throws Exception {
        mockMvc.perform(get("/api/patients")).andExpect(status().isOk()).andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreatePatient() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult result = mockMvc
            .perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(createBody(unique)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("John_" + unique))
            .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        mockMvc.perform(delete("/api/patients/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetPatientById() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult created = mockMvc
            .perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(createBody(unique)))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(get("/api/patients/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.firstName").value("John_" + unique));

        mockMvc.perform(delete("/api/patients/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdatePatient() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult created = mockMvc
            .perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(createBody(unique)))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        String unique2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        mockMvc
            .perform(put("/api/patients/" + id).contentType(MediaType.APPLICATION_JSON).content(createBody(unique2)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("John_" + unique2));

        mockMvc.perform(delete("/api/patients/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeletePatient() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        MvcResult created = mockMvc
            .perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(createBody(unique)))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/patients/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/patients/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentPatient() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/patients/" + randomId)).andExpect(status().isNotFound());
        mockMvc
            .perform(put("/api/patients/" + randomId).contentType(MediaType.APPLICATION_JSON).content(createBody("x")))
            .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/patients/" + randomId)).andExpect(status().isNotFound());
    }
}
