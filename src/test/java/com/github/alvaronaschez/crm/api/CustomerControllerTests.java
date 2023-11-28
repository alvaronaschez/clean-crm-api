package com.github.alvaronaschez.crm.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alvaronaschez.crm.application.UserService;
import com.github.alvaronaschez.crm.application.dto.CustomerOutDTO;
import com.github.alvaronaschez.crm.configuration.SpringProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(SpringProfiles.TEST)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
public class CustomerControllerTests {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

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
    void happyPathTest() throws Exception {
        var admin = this.userService.getActiveUserByUsername("admin").get();
        var result = this.mvc.perform(
                post("/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "joedoe@gmail.com",
                                    "firstName": "Joe",
                                    "lastName": "Doe"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("joedoe@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Joe"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.lastModifiedBy").value(admin.getId().toString()))
                .andReturn();
        // ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = result.getResponse().getContentAsString();
        String customerId = objectMapper.readValue(jsonResponse, CustomerOutDTO.class).getId().toString();
        String customerUrl = String.format("/v1/customers/%s", customerId);
        this.mvc.perform(
                put(customerUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "janedoe@gmail.com",
                                    "firstName": "Jane",
                                    "lastName": "Doe"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.email").value("janedoe@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.lastModifiedBy").value(admin.getId().toString()));
        jsonResponse = this.mvc.perform(get("/v1/customers")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var javaType = objectMapper.getTypeFactory().constructParametricType(List.class,
                CustomerOutDTO.class);
        var customers = objectMapper.readValue(jsonResponse, javaType);
        assertEquals(((List<CustomerOutDTO>) customers).size(), 1);
        this.mvc.perform(delete(customerUrl)).andExpect(status().isNoContent());
        jsonResponse = this.mvc.perform(get("/v1/customers")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(jsonResponse, "[]");
    }
}
