package dev.nilswitt.webmap.api;

import dev.nilswitt.webmap.entities.MapOverlay;
import dev.nilswitt.webmap.entities.repositories.MapOverlayRepository;
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
class MapOverlayControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Autowired
    MapOverlayRepository mapOverlayRepository;

    @Test
    @WithUserDetails("admin")
    void shouldGetAllOverlays() throws UnsupportedEncodingException {
        MapOverlay overlay = new MapOverlay();
        overlay.setName("test-overlay-all");
        overlay.setBasePath("/tiles/test");
        overlay = mapOverlayRepository.save(overlay);

        var result = mockMvcTester.get().uri("/api/map/overlays").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$._embedded.mapOverlayDtoList").asInstanceOf(LIST).hasSizeGreaterThanOrEqualTo(1);

        mapOverlayRepository.delete(overlay);
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetOverlayById() throws UnsupportedEncodingException {
        MapOverlay overlay = new MapOverlay();
        overlay.setName("test-overlay-byid");
        overlay.setBasePath("/tiles/byid");
        overlay = mapOverlayRepository.save(overlay);

        var result = mockMvcTester.get().uri("/api/map/overlays/" + overlay.getId()).exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString().isEqualTo(overlay.getId().toString());
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("test-overlay-byid");
        assertThat(result).bodyJson().extractingPath("$.fullTileUrl").asString().isNotBlank();

        mapOverlayRepository.delete(overlay);
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateOverlay() throws UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "test-overlay-create",
                "basePath", "/tiles/create",
                "tilePathPattern", "/{z}/{x}/{y}.png"
        ));

        var result = mockMvcTester.post().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/map/overlays").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString();
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("test-overlay-create");

        Optional<MapOverlay> created = mapOverlayRepository.findByName("test-overlay-create");
        assertThat(created).isPresent();
        mapOverlayRepository.delete(created.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateOverlay() throws UnsupportedEncodingException {
        MapOverlay overlay = new MapOverlay();
        overlay.setName("test-overlay-update-before");
        overlay.setBasePath("/tiles/old");
        overlay = mapOverlayRepository.save(overlay);

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "test-overlay-update-after",
                "basePath", "/tiles/updated",
                "tilePathPattern", "/{z}/{x}/{y}.webp"
        ));

        var result = mockMvcTester.put().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/map/overlays/" + overlay.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.id").asString().isEqualTo(overlay.getId().toString());

        Optional<MapOverlay> updated = mapOverlayRepository.findById(overlay.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("test-overlay-update-after");
        assertThat(updated.get().getBasePath()).isEqualTo("/tiles/updated");
        assertThat(updated.get().getTilePathPattern()).isEqualTo("/{z}/{x}/{y}.webp");

        mapOverlayRepository.delete(updated.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteOverlay() throws UnsupportedEncodingException {
        MapOverlay overlay = new MapOverlay();
        overlay.setName("test-overlay-delete");
        overlay.setBasePath("/tiles/delete");
        overlay = mapOverlayRepository.save(overlay);

        var result = mockMvcTester.delete().uri("/api/map/overlays/" + overlay.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(mapOverlayRepository.findById(overlay.getId())).isEmpty();
    }
}

