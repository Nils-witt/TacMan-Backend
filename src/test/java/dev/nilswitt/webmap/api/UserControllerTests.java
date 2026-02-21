package dev.nilswitt.webmap.api;


import dev.nilswitt.webmap.entities.User;
import dev.nilswitt.webmap.entities.repositories.UserRepository;
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
class UserControllerTests {

    @Autowired
    MockMvcTester mockMvcTester;

    @Autowired
    UserRepository userRepository;

    @Test
    @WithUserDetails("admin")
    void shouldGetUserById() throws UnsupportedEncodingException {

        User testUser = new User("mockuser", "user@mock.test", "Mock", "User");
        testUser = userRepository.save(testUser);


        var result = mockMvcTester.get().uri("/api/users/" + testUser.getId()).exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$").asMap().containsEntry("id", testUser.getId().toString())
                .containsEntry("username", testUser.getUsername())
                .containsEntry("email", testUser.getEmail())
                .containsEntry("firstName", testUser.getFirstName())
                .containsEntry("lastName", testUser.getLastName());

        userRepository.delete(testUser);
    }

    @Test
    @WithUserDetails("admin")
    void shouldGetAllUsers() throws UnsupportedEncodingException {

        var result = mockMvcTester.get().uri("/api/users").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$._embedded.userDtoList").asInstanceOf(LIST).hasSize((int) userRepository.count());
    }

    @Test
    @WithUserDetails("admin")
    void shouldCreateUser() throws UnsupportedEncodingException {
        User newUser = new User("newuser", "new@mock.test", "New", "User");

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of("username", newUser.getUsername(),
                "email", newUser.getEmail(),
                "firstName", newUser.getFirstName(),
                "lastName", newUser.getLastName()));

        var result = mockMvcTester.post().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/users").exchange();

        assertThat(result).hasStatusOk().bodyJson().extractingPath("$.id").asString();

        assertThat(userRepository.findByUsername(newUser.getUsername())).isPresent();
        userRepository.delete(userRepository.findByUsername(newUser.getUsername()).get());
    }

    @Test
    @WithUserDetails("admin")
    void shouldDeleteUser() throws UnsupportedEncodingException {
        User newUser = new User("deleteuser", "new@mock.test", "New", "User");
        userRepository.save(newUser);
        var result = mockMvcTester.delete().uri("/api/users/" + newUser.getId().toString()).exchange();

        assertThat(result).hasStatusOk();

        assertThat(userRepository.findByUsername(newUser.getUsername())).isEmpty();
    }

    @Test
    @WithUserDetails("admin")
    void shouldUpdateUser() throws UnsupportedEncodingException {
        User newUser = new User("deleteuser", "asdasdas@mock.test", "New", "User");
        newUser = userRepository.save(newUser);

        String newEmail = "user11111@mock.test";
        String newFirstName = "Updated";
        String newLastName = "Udks";
        String newUsername = "updateduser";

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(Map.of("username", newUsername,
                "email", newEmail,
                "firstName", newFirstName,
                "lastName", newLastName));

        var result = mockMvcTester.put().contentType(MediaType.APPLICATION_JSON).content(payload).uri("/api/users/" + newUser.getId().toString()).exchange();
        System.out.println(result.getResponse().getContentAsString());
        /*
        Assert persistence of changes in the database after update
         */
        Optional<User> updatedUserOpt = userRepository.findById(newUser.getId());
        assertThat(updatedUserOpt).isPresent();
        User updatedUser = updatedUserOpt.get();
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        assertThat(updatedUser.getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedUser.getLastName()).isEqualTo(newLastName);
        assertThat(updatedUser.getUsername()).isEqualTo(newUsername);

        /*
            Assert response status and content
         */
        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.id").asString().isEqualTo(newUser.getId().toString());
        assertThat(result).bodyJson().extractingPath("$.username").asString().isEqualTo(newUsername);
        assertThat(result).bodyJson().extractingPath("$.email").asString().isEqualTo(newEmail);
        assertThat(result).bodyJson().extractingPath("$.firstName").asString().isEqualTo(newFirstName);
        assertThat(result).bodyJson().extractingPath("$.lastName").asString().isEqualTo(newLastName);

    }
}