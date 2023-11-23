package com.github.alvaronaschez.crm.configuration;

import com.github.alvaronaschez.crm.api.ApiDocsProps;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@OpenAPIDefinition
public class SwaggerConfig {

    private final ApiDocsProps properties;
    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI springOpenAPI() {
        Components components = new Components();
        components.addSecuritySchemes("cookieAuth",
                new SecurityScheme().name("SESSION").type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.COOKIE));

        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8080")
                                .description("Development server (development environment)")))
                .info(new Info().title(this.properties.getTitle())
                        .description(this.properties.getDescription())
                        .version(buildProperties.getVersion())
                        .license(new License().name(this.properties.getLicenseName()).url(this.properties.getLicenseUrl()))
                        .contact(new Contact().name(this.properties.getName()).url(this.properties.getWebpage())))
                .components(components)
                .security(List.of(new SecurityRequirement()
                        .addList("cookieAuth")));
    }
}