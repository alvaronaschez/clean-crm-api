package com.github.alvaronaschez.crm.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.github.alvaronaschez.crm.configuration.SpringProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(SpringProfiles.TEST)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
public class CustomerControllerTests {
    @Autowired
    protected MockMvc mvc;

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @Test
    @Order(1)
    void unauthenticatedRequestForbidden() throws Exception {
        this.mvc.perform(get("/v1/customers")).andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    @WithUserDetails(value = "admin")
    void create() throws Exception {
        var result = this.mvc.perform(
                post("/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "joedoe",
                                    "firstName": "Joe",
                                    "lastName": "Doe"
                                }
                                """))
                .andExpect(status().isCreated()).andReturn();
    }
}
