package dev.nilswitt.tacman.api;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-test.properties",
    properties = {
        "application.admin.create=true",
        "application.admin.username=admin",
        "application.admin.password=admin",
        "application.photos.path=${java.io.tmpdir}/tacman-photo-tests",
    }
)
class PhotoControllerTests {

    @Autowired
    MockMvc mockMvc;

    private MvcResult createMissionGroup(String unique) throws Exception {
        return mockMvc
            .perform(
                post("/api/missiongroups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "Mission_%s", "startTime": "2024-01-01T00:00:00Z", "unitIds": [], "mapGroupIds": []}
                        """.formatted(unique)
                    )
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult uploadPhoto(String missionId) throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        return mockMvc
            .perform(
                MockMvcRequestBuilders.multipart("/api/photos")
                    .file(file)
                    .param("latitude", "48.0")
                    .param("longitude", "11.0")
                    .param("missionGroupId", missionId)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    @WithUserDetails("admin")
    void shouldListPhotos() throws Exception {
        mockMvc.perform(get("/api/photos")).andExpect(status().isOk()).andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithUserDetails("admin")
    void shouldUploadPhoto() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String missionId = JsonPath.read(createMissionGroup(unique).getResponse().getContentAsString(), "$.id");

        MvcResult result = uploadPhoto(missionId);

        String photoId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        mockMvc.perform(get("/api/photos/" + photoId)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(photoId));

        // cleanup
        mockMvc.perform(delete("/api/photos/" + photoId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/missiongroups/" + missionId)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldPatchPhotoName() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String missionId = JsonPath.read(createMissionGroup(unique).getResponse().getContentAsString(), "$.id");
        String photoId = JsonPath.read(uploadPhoto(missionId).getResponse().getContentAsString(), "$.id");

        mockMvc
            .perform(
                patch("/api/photos/" + photoId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name": "updated_%s"}
                        """.formatted(unique))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated_" + unique));

        // cleanup
        mockMvc.perform(delete("/api/photos/" + photoId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/missiongroups/" + missionId)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeletePhoto() throws Exception {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String missionId = JsonPath.read(createMissionGroup(unique).getResponse().getContentAsString(), "$.id");
        String photoId = JsonPath.read(uploadPhoto(missionId).getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/photos/" + photoId)).andExpect(status().isOk());
        mockMvc.perform(get("/api/photos/" + photoId)).andExpect(status().isNotFound());

        // cleanup
        mockMvc.perform(delete("/api/missiongroups/" + missionId)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturn404ForNonExistentPhoto() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/photos/" + randomId)).andExpect(status().isNotFound());
        mockMvc
            .perform(patch("/api/photos/" + randomId).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/photos/" + randomId)).andExpect(status().isNotFound());
    }
}
