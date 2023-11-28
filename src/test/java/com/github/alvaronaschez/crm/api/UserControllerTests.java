package com.github.alvaronaschez.crm.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.github.alvaronaschez.crm.application.UserService;
import com.github.alvaronaschez.crm.configuration.SpringProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(SpringProfiles.TEST)
@Testcontainers
class UserControllerTests {
    @Autowired
    protected MockMvc mvc;

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);
    @Autowired
    UserService userService;

    @Test
    @WithUserDetails(value = "admin")
    void getUserById() throws Exception {
        var user = this.userService.getActiveUserByUsername("user");
        this.mvc.perform(get("/v1/users/" + user.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.active").value(user.isActive()))
                .andExpect(jsonPath("$.roles").isEmpty());

    }

    @Test
    @WithMockUser(username = "joedoe", roles = {})
    void getUserByIdWithNoAdmin() throws Exception {
        var user = this.userService.getActiveUserByUsername("user");
        this.mvc.perform(get("/v1/users/" + user.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
