package dev.nilswitt.webmap.api;

import dev.nilswitt.webmap.entities.Unit;
import dev.nilswitt.webmap.entities.repositories.UnitRepository;
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
class UnitControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Autowired
    UnitRepository unitRepository;

    @Test
    @WithUserDetails("admin")
    void shouldGetAllUnits() throws UnsupportedEncodingException {
        Unit unit = new Unit();
        unit.setName("testunit-all");
        unit = unitRepository.save(unit);

        var result = mockMvcTester.get().uri("/api/units").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$._embedded.unitDtoList").asInstanceOf(LIST).hasSizeGreaterThanOrEqualTo(1);

        unitRepository.delete(unit);
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetUnitById() throws UnsupportedEncodingException {
        Unit unit = new Unit();
        unit.setName("testunit-byid");
        unit = unitRepository.save(unit);

        var result = mockMvcTester.get().uri("/api/units/" + unit.getId()).exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString().isEqualTo(unit.getId().toString());
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("testunit-byid");

        unitRepository.delete(unit);
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateUnit() throws UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "testunit-create",
                "status", 6,
                "speakRequest", false,
                "showOnMap", false
        ));

        var result = mockMvcTester.post().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/units").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString();
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("testunit-create");

        Optional<Unit> created = unitRepository.findByName("testunit-create");
        assertThat(created).isPresent();
        unitRepository.delete(created.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateUnit() throws UnsupportedEncodingException {
        Unit unit = new Unit();
        unit.setName("testunit-update-before");
        unit = unitRepository.save(unit);

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "testunit-update-after",
                "status", 3,
                "speakRequest", true,
                "showOnMap", false,
                "position", Map.of(
                        "latitude", 51.0,
                        "longitude", 7.0,
                        "altitude", 100.0
                )
        ));

        var result = mockMvcTester.put().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/units/" + unit.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("testunit-update-after");
        assertThat(result).bodyJson().extractingPath("$.status").convertTo(Integer.class).isEqualTo(3);
        assertThat(result).bodyJson().extractingPath("$.speakRequest").convertTo(Boolean.class).isEqualTo(true);

        Optional<Unit> updated = unitRepository.findById(unit.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("testunit-update-after");
        assertThat(updated.get().getStatus()).isEqualTo(3);
        assertThat(updated.get().isSpeakRequest()).isEqualTo(true);

        unitRepository.delete(updated.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldPatchUnit() throws UnsupportedEncodingException {
        Unit unit = new Unit();
        unit.setName("testunit-patch-before");
        unit = unitRepository.save(unit);

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of(
                "name", "testunit-patch-after",
                "speakRequest", true
        ));

        var result = mockMvcTester.patch().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/units/" + unit.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").asString().isEqualTo("testunit-patch-after");
        assertThat(result).bodyJson().extractingPath("$.speakRequest").convertTo(Boolean.class).isEqualTo(true);

        Optional<Unit> patched = unitRepository.findById(unit.getId());
        assertThat(patched).isPresent();
        assertThat(patched.get().getName()).isEqualTo("testunit-patch-after");
        assertThat(patched.get().isSpeakRequest()).isEqualTo(true);

        unitRepository.delete(patched.get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteUnit() throws UnsupportedEncodingException {
        Unit unit = new Unit();
        unit.setName("testunit-delete");
        unit = unitRepository.save(unit);

        var result = mockMvcTester.delete().uri("/api/units/" + unit.getId()).exchange();

        assertThat(result).hasStatusOk();
        assertThat(unitRepository.findById(unit.getId())).isEmpty();
    }
}




