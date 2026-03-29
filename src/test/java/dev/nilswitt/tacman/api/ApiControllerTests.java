package dev.nilswitt.tacman.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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
class ApiControllerTests {

    @Autowired
    MockMvc mockMvc;

    private static Stream<Arguments> securedEndpoints() {
        String randomId = UUID.randomUUID().toString();
        return Stream.of(
                Arguments.of("GET", "/api/profile"),
                Arguments.of("GET", "/api/users"),
                Arguments.of("POST", "/api/users"),
                Arguments.of("GET", "/api/users/" + randomId),
                Arguments.of("PUT", "/api/users/" + randomId),
                Arguments.of("DELETE", "/api/users/" + randomId),
                Arguments.of("GET", "/api/units"),
                Arguments.of("POST", "/api/units"),
                Arguments.of("GET", "/api/units/" + randomId),
                Arguments.of("PATCH", "/api/units/" + randomId),
                Arguments.of("DELETE", "/api/units/" + randomId),
                Arguments.of("GET", "/api/units/" + randomId + "/status/history"),
                Arguments.of("GET", "/api/units/" + randomId + "/position/history"),
                Arguments.of("GET", "/api/missiongroups"),
                Arguments.of("POST", "/api/missiongroups"),
                Arguments.of("GET", "/api/missiongroups/" + randomId),
                Arguments.of("PUT", "/api/missiongroups/" + randomId),
                Arguments.of("DELETE", "/api/missiongroups/" + randomId),
                Arguments.of("GET", "/api/photos"),
                Arguments.of("GET", "/api/photos/" + randomId),
                Arguments.of("PATCH", "/api/photos/" + randomId),
                Arguments.of("DELETE", "/api/photos/" + randomId),
                Arguments.of("GET", "/api/photos/" + randomId + "/image"),
                Arguments.of("GET", "/api/map"),
                Arguments.of("GET", "/api/map/baselayers"),
                Arguments.of("POST", "/api/map/baselayers"),
                Arguments.of("GET", "/api/map/baselayers/" + randomId),
                Arguments.of("PUT", "/api/map/baselayers/" + randomId),
                Arguments.of("DELETE", "/api/map/baselayers/" + randomId),
                Arguments.of("GET", "/api/map/overlays"),
                Arguments.of("POST", "/api/map/overlays"),
                Arguments.of("GET", "/api/map/overlays/" + randomId),
                Arguments.of("PUT", "/api/map/overlays/" + randomId),
                Arguments.of("DELETE", "/api/map/overlays/" + randomId),
                Arguments.of("GET", "/api/map/items"),
                Arguments.of("POST", "/api/map/items"),
                Arguments.of("GET", "/api/map/items/" + randomId),
                Arguments.of("PUT", "/api/map/items/" + randomId),
                Arguments.of("DELETE", "/api/map/items/" + randomId),
                Arguments.of("GET", "/api/map/groups"),
                Arguments.of("POST", "/api/map/groups"),
                Arguments.of("GET", "/api/map/groups/" + randomId),
                Arguments.of("PUT", "/api/map/groups/" + randomId),
                Arguments.of("DELETE", "/api/map/groups/" + randomId),
                Arguments.of("GET", "/api/securitygroups"),
                Arguments.of("POST", "/api/securitygroups"),
                Arguments.of("GET", "/api/securitygroups/" + randomId),
                Arguments.of("PUT", "/api/securitygroups/" + randomId),
                Arguments.of("DELETE", "/api/securitygroups/" + randomId)
        );
    }

    private MockHttpServletRequestBuilder buildRequest(
            String method,
            String path
    ) {
        return switch (method) {
            case "GET" -> get(path);
            case "POST" -> post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            case "PUT" -> put(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            case "PATCH" -> patch(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}");
            case "DELETE" -> delete(path);
            default -> throw new IllegalArgumentException(
                    "Unsupported method: " + method
            );
        };
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetApiRoot() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.users").exists())
                .andExpect(jsonPath("$._links.units").exists())
                .andExpect(jsonPath("$._links.map").exists())
                .andExpect(jsonPath("$._links.missionGroups").exists())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isNotBlank();
    }

    @Test
    void shouldRejectValidateTokenWithoutAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/token")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldIssueAndValidateTokenForAdmin() throws Exception {
        String body = """
                {
                  "username": "admin",
                  "password": "admin"
                }
                """;

        MvcResult obtainResult = mockMvc
                .perform(
                        post("/api/token").contentType(MediaType.APPLICATION_JSON).content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andReturn();

        String token = JsonPath.read(
                obtainResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                "$.token"
        );

        mockMvc
                .perform(get("/api/token").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.user").exists());
    }

    @ParameterizedTest(name = "{0} {1} requires authentication")
    @MethodSource("securedEndpoints")
    void shouldRequireAuthenticationForAllSecuredEndpoints(
            String method,
            String path
    ) throws Exception {
        mockMvc
                .perform(buildRequest(method, path))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRequireAuthenticationForPhotoUploadEndpoint() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc
                .perform(
                        MockMvcRequestBuilders.multipart("/api/photos")
                                .file(file)
                                .param("latitude", "10.0")
                                .param("longitude", "20.0")
                                .param("missionGroupId", UUID.randomUUID().toString())
                )
                .andExpect(status().isUnauthorized());
    }
}
