package dev.nilswitt.tacman.api.map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    "application.admin.create=true",
    "application.admin.username=admin",
    "application.admin.password=admin",
  }
)
class MapBaseLayerControllerTests {

  @Autowired
  MockMvc mockMvc;

  @Test
  @WithUserDetails("admin")
  void shouldListBaseLayers() throws Exception {
    mockMvc
      .perform(get("/api/map/baselayers"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._links.self").exists());
  }

  @Test
  @WithUserDetails("admin")
  void shouldCreateBaseLayer() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String body = """
      {
        "name": "Layer_%s",
        "url": "https://tiles.example.com/{z}/{x}/{y}.png",
        "cacheUrl": null
      }
      """.formatted(unique);

    MvcResult result = mockMvc
      .perform(
        post("/api/map/baselayers")
          .contentType(MediaType.APPLICATION_JSON)
          .content(body)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.name").value("Layer_" + unique))
      .andReturn();

    String id = JsonPath.read(
      result.getResponse().getContentAsString(),
      "$.id"
    );

    // cleanup
    mockMvc
      .perform(delete("/api/map/baselayers/" + id))
      .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails("admin")
  void shouldGetBaseLayerById() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String body = """
      {
        "name": "Layer_%s",
        "url": "https://tiles.example.com/{z}/{x}/{y}.png",
        "cacheUrl": null
      }
      """.formatted(unique);

    MvcResult created = mockMvc
      .perform(
        post("/api/map/baselayers")
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
      .perform(get("/api/map/baselayers/" + id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.name").value("Layer_" + unique));

    // cleanup
    mockMvc
      .perform(delete("/api/map/baselayers/" + id))
      .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails("admin")
  void shouldUpdateBaseLayer() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String createBody = """
      {
        "name": "Layer_%s",
        "url": "https://tiles.example.com/{z}/{x}/{y}.png",
        "cacheUrl": null
      }
      """.formatted(unique);

    MvcResult created = mockMvc
      .perform(
        post("/api/map/baselayers")
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
        "name": "Layer_%s_updated",
        "url": "https://updated.example.com/{z}/{x}/{y}.png",
        "cacheUrl": "https://cache.example.com/{z}/{x}/{y}.png"
      }
      """.formatted(unique);

    mockMvc
      .perform(
        put("/api/map/baselayers/" + id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(updateBody)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Layer_" + unique + "_updated"))
      .andExpect(
        jsonPath("$.url").value("https://updated.example.com/{z}/{x}/{y}.png")
      );

    // cleanup
    mockMvc
      .perform(delete("/api/map/baselayers/" + id))
      .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails("admin")
  void shouldDeleteBaseLayer() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String body = """
      {
        "name": "Layer_%s",
        "url": "https://tiles.example.com/{z}/{x}/{y}.png",
        "cacheUrl": null
      }
      """.formatted(unique);

    MvcResult created = mockMvc
      .perform(
        post("/api/map/baselayers")
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
      .perform(delete("/api/map/baselayers/" + id))
      .andExpect(status().isOk());

    mockMvc
      .perform(get("/api/map/baselayers/" + id))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithUserDetails("admin")
  void shouldReturn404ForNonExistentBaseLayer() throws Exception {
    String randomId = UUID.randomUUID().toString();

    mockMvc
      .perform(get("/api/map/baselayers/" + randomId))
      .andExpect(status().isNotFound());

    mockMvc
      .perform(
        put("/api/map/baselayers/" + randomId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
            {"name": "x", "url": "https://example.com", "cacheUrl": null}
            """
          )
      )
      .andExpect(status().isNotFound());

    mockMvc
      .perform(delete("/api/map/baselayers/" + randomId))
      .andExpect(status().isNotFound());
  }
}
