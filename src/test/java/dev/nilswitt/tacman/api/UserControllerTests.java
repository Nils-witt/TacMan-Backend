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
class UserControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldListUsers() throws Exception {
        mockMvc
                .perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateUser() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String body = """
                {
                  "username": "u_%s",
                  "email": "%s@test.com",
                  "firstName": "First",
                  "lastName": "Last",
                  "enabled": true,
                  "locked": false
                }
                """.formatted(unique, unique);

        MvcResult result = mockMvc
                .perform(
                        post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("u_" + unique))
                .andReturn();

        String id = JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.id"
        );

        // cleanup
        mockMvc.perform(delete("/api/users/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetUserById() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String body = """
                {
                  "username": "u_%s",
                  "email": "%s@test.com",
                  "firstName": "First",
                  "lastName": "Last",
                  "enabled": true,
                  "locked": false
                }
                """.formatted(unique, unique);

        MvcResult created = mockMvc
                .perform(
                        post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body)
                )
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(
                created.getResponse().getContentAsString(),
                "$.id"
        );

        mockMvc
                .perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value("u_" + unique));

        // cleanup
        mockMvc.perform(delete("/api/users/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateUser() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String createBody = """
                {
                  "username": "u_%s",
                  "email": "%s@test.com",
                  "firstName": "First",
                  "lastName": "Last",
                  "enabled": true,
                  "locked": false
                }
                """.formatted(unique, unique);

        MvcResult created = mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(
                created.getResponse().getContentAsString(),
                "$.id"
        );

        String unique2 = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String updateBody = """
                {
                  "username": "u2_%s",
                  "email": "%s@updated.com",
                  "firstName": "Updated",
                  "lastName": "Name",
                  "enabled": true,
                  "locked": false
                }
                """.formatted(unique2, unique2);

        mockMvc
                .perform(
                        put("/api/users/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("u2_" + unique2))
                .andExpect(jsonPath("$.firstName").value("Updated"));

        // cleanup
        mockMvc.perform(delete("/api/users/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteUser() throws Exception {
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
        String body = """
                {
                  "username": "u_%s",
                  "email": "%s@test.com",
                  "firstName": "First",
                  "lastName": "Last",
                  "enabled": true,
                  "locked": false
                }
                """.formatted(unique, unique);

        MvcResult created = mockMvc
                .perform(
                        post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body)
                )
                .andExpect(status().isOk())
                .andReturn();

        String id = JsonPath.read(
                created.getResponse().getContentAsString(),
                "$.id"
        );

        mockMvc.perform(delete("/api/users/" + id)).andExpect(status().isOk());

        mockMvc.perform(get("/api/users/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentUser() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc
                .perform(get("/api/users/" + randomId))
                .andExpect(status().isNotFound());

        mockMvc
                .perform(
                        put("/api/users/" + randomId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isNotFound());

        mockMvc
                .perform(delete("/api/users/" + randomId))
                .andExpect(status().isNotFound());
    }
}
