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
class SecurityGroupControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldListSecurityGroups() throws Exception {
        mockMvc
            .perform(get("/api/securitygroups"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateSecurityGroup() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Group_%s",
              "ssoGroupName": "",
              "roles": ["UNIT_VIEW"]
            }
            """.formatted(unique);

        MvcResult result = mockMvc
            .perform(post("/api/securitygroups").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Group_" + unique))
            .andExpect(jsonPath("$.roles[0]").value("UNIT_VIEW"))
            .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // cleanup
        mockMvc.perform(delete("/api/securitygroups/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetSecurityGroupById() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Group_%s",
              "ssoGroupName": "",
              "roles": []
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/securitygroups").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(get("/api/securitygroups/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Group_" + unique));

        // cleanup
        mockMvc.perform(delete("/api/securitygroups/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateSecurityGroup() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String createBody = """
            {
              "name": "Group_%s",
              "ssoGroupName": "",
              "roles": []
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/securitygroups").contentType(MediaType.APPLICATION_JSON).content(createBody))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        String updateBody = """
            {
              "name": "Group_%s_updated",
              "ssoGroupName": "sso-group",
              "roles": ["UNIT_VIEW", "UNIT_EDIT"]
            }
            """.formatted(unique);

        mockMvc
            .perform(put("/api/securitygroups/" + id).contentType(MediaType.APPLICATION_JSON).content(updateBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Group_" + unique + "_updated"))
            .andExpect(jsonPath("$.ssoGroupName").value("sso-group"));

        // cleanup
        mockMvc.perform(delete("/api/securitygroups/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteSecurityGroup() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Group_%s",
              "ssoGroupName": "",
              "roles": []
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/securitygroups").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/securitygroups/" + id)).andExpect(status().isOk());

        mockMvc.perform(get("/api/securitygroups/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentSecurityGroup() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/securitygroups/" + randomId)).andExpect(status().isNotFound());

        mockMvc
            .perform(
                put("/api/securitygroups/" + randomId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"x\", \"ssoGroupName\": \"\", \"roles\": []}")
            )
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/securitygroups/" + randomId)).andExpect(status().isNotFound());
    }
}
