package dev.nilswitt.webmap.api;

import dev.nilswitt.webmap.entities.MapItem;
import dev.nilswitt.webmap.entities.repositories.MapItemRepository;
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
class MapItemControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Autowired
    MapItemRepository mapItemRepository;

    @Test
    @WithUserDetails("admin")
    void shouldGetAllMapItems() throws UnsupportedEncodingException {
        MapItem item = new MapItem();
        item.setName("test-item-all");
        item = mapItemRepository.save(item);

        var result = mockMvcTester.get().uri("/api/map/items").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$._embedded.mapItemDtoList").asInstanceOf(LIST).hasSizeGreaterThanOrEqualTo(1);

        mapItemRepository.delete(item);
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetMapItemById() throws UnsupportedEncodingException {
        MapItem item = new MapItem();
        item.setName("test-item-byid");
        item = mapItemRepository.save(item);

        var result = mockMvcTester.get().uri("/api/map/items/" + item.getId()).exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString().isEqualTo(item.getId().toString());
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("test-item-byid");

        mapItemRepository.delete(item);
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateMapItem() throws UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "test-item-create",
                "position", Map.of(
                        "latitude", 51.0,
                        "longitude", 7.0,
                        "altitude", 100.0
                )
        ));

        var result = mockMvcTester.post().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/map/items").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString();
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("test-item-create");

        Optional<MapItem> created = mapItemRepository.findByName("test-item-create");
        assertThat(created).isPresent();
        mapItemRepository.delete(created.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateMapItem() throws UnsupportedEncodingException {
        MapItem item = new MapItem();
        item.setName("test-item-update-before");
        item = mapItemRepository.save(item);

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "test-item-update-after",
                "position", Map.of(
                        "latitude", 52.0,
                        "longitude", 8.0,
                        "altitude", 200.0
                )
        ));

        var result = mockMvcTester.put().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/map/items/" + item.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("test-item-update-after");

        Optional<MapItem> updated = mapItemRepository.findById(item.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("test-item-update-after");
        assertThat(updated.get().getPosition().getLatitude()).isEqualTo(52.0);
        assertThat(updated.get().getPosition().getLongitude()).isEqualTo(8.0);

        mapItemRepository.delete(updated.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteMapItem() throws UnsupportedEncodingException {
        MapItem item = new MapItem();
        item.setName("test-item-delete");
        item = mapItemRepository.save(item);

        var result = mockMvcTester.delete().uri("/api/map/items/" + item.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(mapItemRepository.findById(item.getId())).isEmpty();
    }
}

