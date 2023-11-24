package com.github.alvaronaschez.crm.api;

import com.github.alvaronaschez.crm.configuration.SpringProfiles;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
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

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(SpringProfiles.TEST)
@Testcontainers
class AuthControllerTests {
        @Autowired
        private MockMvc mvc;

        @Container
        @ServiceConnection
        static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
                        .withExposedPorts(6379);

        @Test
        @WithUserDetails(value = "admin")
        void me() throws Exception {
                this.mvc.perform(get("/v1/me"))
                                .andExpect(status().isOk());
        }

        @Test
        void login() throws Exception {
                var result = this.mvc.perform(
                                post("/v1/login").servletPath("/v1/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                        {
                                                                                "username": "admin",
                                                                                "password": "secret"
                                                                        }
                                                                """))
                                .andExpect(status().isNoContent()).andReturn();

                var cookie = Arrays.stream(result.getResponse().getCookies()).findFirst().get();
                this.mvc.perform(
                                get("/v1/me")
                                                .cookie(new Cookie(cookie.getName(), cookie.getValue())))
                                .andExpect(status().isOk());
        }

        @Test
        void logout() throws Exception {
                var result = this.mvc.perform(
                                post("/v1/login").servletPath("/v1/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                        {
                                                                                "username": "admin",
                                                                                "password": "secret"
                                                                        }
                                                                """))
                                .andExpect(status().isNoContent()).andReturn();

                var cookie = Arrays.stream(result.getResponse().getCookies()).findFirst().get();

                this.mvc.perform(
                                get("/v1/me")
                                                .cookie(new Cookie(cookie.getName(), cookie.getValue())))
                                .andExpect(status().isOk());
                this.mvc.perform(
                                post("/v1/logout")
                                                .cookie(new Cookie(cookie.getName(), cookie.getValue())))
                                .andExpect(status().isOk());
                this.mvc.perform(
                                get("/v1/me")
                                                .cookie(new Cookie(cookie.getName(), cookie.getValue())))
                                .andExpect(status().isUnauthorized());
        }

}
