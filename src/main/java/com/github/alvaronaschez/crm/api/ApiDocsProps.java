package com.github.alvaronaschez.crm.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "apidocs")
public class ApiDocsProps {
    private String title;
    private String description;
    private String name;
    private String webpage;
    private String termsOfService;
    private String licenseName;
    private String licenseUrl;
}