package dev.nilswitt.tacman.api.map;

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
class MapOverlayControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    void shouldListOverlays() throws Exception {
        mockMvc
            .perform(get("/api/map/overlays"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateOverlay() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Overlay_%s",
              "baseUrl": "https://overlay.example.com",
              "basePath": "tiles",
              "tilePathPattern": "{z}/{x}/{y}.png",
              "layerVersion": 0
            }
            """.formatted(unique);

        MvcResult result = mockMvc
            .perform(post("/api/map/overlays").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Overlay_" + unique))
            .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // cleanup
        mockMvc.perform(delete("/api/map/overlays/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetOverlayById() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Overlay_%s",
              "baseUrl": "https://overlay.example.com",
              "basePath": "tiles",
              "tilePathPattern": "{z}/{x}/{y}.png",
              "layerVersion": 0
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/map/overlays").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(get("/api/map/overlays/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Overlay_" + unique));

        // cleanup
        mockMvc.perform(delete("/api/map/overlays/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateOverlay() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String createBody = """
            {
              "name": "Overlay_%s",
              "baseUrl": "https://overlay.example.com",
              "basePath": "tiles",
              "tilePathPattern": "{z}/{x}/{y}.png",
              "layerVersion": 0
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/map/overlays").contentType(MediaType.APPLICATION_JSON).content(createBody))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        String updateBody = """
            {
              "name": "Overlay_%s_updated",
              "baseUrl": "https://updated.example.com",
              "basePath": "newtiles",
              "tilePathPattern": "{z}/{x}/{y}.jpg",
              "layerVersion": 0
            }
            """.formatted(unique);

        mockMvc
            .perform(put("/api/map/overlays/" + id).contentType(MediaType.APPLICATION_JSON).content(updateBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Overlay_" + unique + "_updated"))
            .andExpect(jsonPath("$.baseUrl").value("https://updated.example.com"));

        // cleanup
        mockMvc.perform(delete("/api/map/overlays/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteOverlay() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String body = """
            {
              "name": "Overlay_%s",
              "baseUrl": "https://overlay.example.com",
              "basePath": "tiles",
              "tilePathPattern": "{z}/{x}/{y}.png",
              "layerVersion": 0
            }
            """.formatted(unique);

        MvcResult created = mockMvc
            .perform(post("/api/map/overlays").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/map/overlays/" + id)).andExpect(status().isOk());

        mockMvc.perform(get("/api/map/overlays/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentOverlay() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/map/overlays/" + randomId)).andExpect(status().isNotFound());

        mockMvc
            .perform(
                put("/api/map/overlays/" + randomId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "x", "baseUrl": "https://example.com", "basePath": "", "tilePathPattern": "{z}/{x}/{y}.png", "layerVersion": 0}
                        """
                    )
            )
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/map/overlays/" + randomId)).andExpect(status().isNotFound());
    }
}
