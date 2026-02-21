package dev.nilswitt.webmap.api;

import dev.nilswitt.webmap.entities.MapBaseLayer;
import dev.nilswitt.webmap.entities.repositories.MapBaseLayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class MapBaseLayerControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Autowired
    MapBaseLayerRepository mapBaseLayerRepository;

    @Test
    @WithUserDetails("admin")
    void shouldGetAllBaseLayers() throws UnsupportedEncodingException {
        MapBaseLayer layer = new MapBaseLayer();
        layer.setName("test-baselayer-all");
        layer.setUrl("https://example.com/tiles");
        layer = mapBaseLayerRepository.save(layer);

        var result = mockMvcTester.get().uri("/api/map/baselayers").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$._embedded.mapBaseLayerDtoList").asInstanceOf(LIST).hasSizeGreaterThanOrEqualTo(1);

        mapBaseLayerRepository.delete(layer);
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetBaseLayerById() throws UnsupportedEncodingException {
        MapBaseLayer layer = new MapBaseLayer();
        layer.setName("test-baselayer-byid");
        layer.setUrl("https://example.com/tiles");
        layer = mapBaseLayerRepository.save(layer);

        var result = mockMvcTester.get().uri("/api/map/baselayers/" + layer.getId()).exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString().isEqualTo(layer.getId().toString());
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("test-baselayer-byid");
        assertThat(result).bodyJson().extractingPath("$.url").asString().isEqualTo("https://example.com/tiles");

        mapBaseLayerRepository.delete(layer);
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateBaseLayer() throws UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "test-baselayer-create",
                "url", "https://example.com/new-tiles"
        ));

        var result = mockMvcTester.post().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/map/baselayers").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString();
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("test-baselayer-create");
        assertThat(result).bodyJson().extractingPath("$.url").asString().isEqualTo("https://example.com/new-tiles");

        Optional<MapBaseLayer> created = mapBaseLayerRepository.findByName("test-baselayer-create");
        assertThat(created).isPresent();
        mapBaseLayerRepository.delete(created.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateBaseLayer() throws UnsupportedEncodingException {
        MapBaseLayer layer = new MapBaseLayer();
        layer.setName("test-baselayer-update-before");
        layer.setUrl("https://example.com/old");
        layer = mapBaseLayerRepository.save(layer);

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "test-baselayer-update-after",
                "url", "https://example.com/updated"
        ));

        var result = mockMvcTester.put().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/map/baselayers/" + layer.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.id").asString().isEqualTo(layer.getId().toString());

        Optional<MapBaseLayer> updated = mapBaseLayerRepository.findById(layer.getId());
        assertThat(updated).isPresent();

        mapBaseLayerRepository.delete(updated.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteBaseLayer() throws UnsupportedEncodingException {
        MapBaseLayer layer = new MapBaseLayer();
        layer.setName("test-baselayer-delete");
        layer.setUrl("https://example.com/delete");
        layer = mapBaseLayerRepository.save(layer);

        var result = mockMvcTester.delete().uri("/api/map/baselayers/" + layer.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(mapBaseLayerRepository.findById(layer.getId())).isEmpty();
    }
}

