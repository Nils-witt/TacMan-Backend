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
class MapItemControllerTests {

  @Autowired
  MockMvc mockMvc;

  @Test
  @WithUserDetails("admin")
  void shouldListMapItems() throws Exception {
    mockMvc
      .perform(get("/api/map/items"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._links.self").exists());
  }

  @Test
  @WithUserDetails("admin")
  void shouldCreateMapItem() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String body = """
      {
        "name": "Item_%s",
        "position": {
          "latitude": 48.8566,
          "longitude": 2.3522
        },
        "zoomLevel": 12
      }
      """.formatted(unique);

    MvcResult result = mockMvc
      .perform(
        post("/api/map/items")
          .contentType(MediaType.APPLICATION_JSON)
          .content(body)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").exists())
      .andExpect(jsonPath("$.name").value("Item_" + unique))
      .andReturn();

    String id = JsonPath.read(
      result.getResponse().getContentAsString(),
      "$.id"
    );

    // cleanup
    mockMvc.perform(delete("/api/map/items/" + id)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails("admin")
  void shouldGetMapItemById() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String body = """
      {
        "name": "Item_%s",
        "position": {
          "latitude": 48.8566,
          "longitude": 2.3522
        },
        "zoomLevel": 12
      }
      """.formatted(unique);

    MvcResult created = mockMvc
      .perform(
        post("/api/map/items")
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
      .perform(get("/api/map/items/" + id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.name").value("Item_" + unique));

    // cleanup
    mockMvc.perform(delete("/api/map/items/" + id)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails("admin")
  void shouldUpdateMapItem() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String createBody = """
      {
        "name": "Item_%s",
        "position": {
          "latitude": 48.8566,
          "longitude": 2.3522
        },
        "zoomLevel": 12
      }
      """.formatted(unique);

    MvcResult created = mockMvc
      .perform(
        post("/api/map/items")
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
        "name": "Item_%s_updated",
        "position": {
          "latitude": 51.5074,
          "longitude": -0.1278
        },
        "zoomLevel": 15
      }
      """.formatted(unique);

    mockMvc
      .perform(
        put("/api/map/items/" + id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(updateBody)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Item_" + unique + "_updated"))
      .andExpect(jsonPath("$.zoomLevel").value(15));

    // cleanup
    mockMvc.perform(delete("/api/map/items/" + id)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails("admin")
  void shouldDeleteMapItem() throws Exception {
    String unique = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 10);
    String body = """
      {
        "name": "Item_%s",
        "position": {
          "latitude": 48.8566,
          "longitude": 2.3522
        },
        "zoomLevel": 12
      }
      """.formatted(unique);

    MvcResult created = mockMvc
      .perform(
        post("/api/map/items")
          .contentType(MediaType.APPLICATION_JSON)
          .content(body)
      )
      .andExpect(status().isOk())
      .andReturn();

    String id = JsonPath.read(
      created.getResponse().getContentAsString(),
      "$.id"
    );

    mockMvc.perform(delete("/api/map/items/" + id)).andExpect(status().isOk());

    mockMvc
      .perform(get("/api/map/items/" + id))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithUserDetails("admin")
  void shouldReturn404ForNonExistentMapItem() throws Exception {
    String randomId = UUID.randomUUID().toString();

    mockMvc
      .perform(get("/api/map/items/" + randomId))
      .andExpect(status().isNotFound());

    mockMvc
      .perform(
        put("/api/map/items/" + randomId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
            {"name": "x", "position": {"latitude": 0.0, "longitude": 0.0}, "zoomLevel": 1}
            """
          )
      )
      .andExpect(status().isNotFound());

    mockMvc
      .perform(delete("/api/map/items/" + randomId))
      .andExpect(status().isNotFound());
  }
}
