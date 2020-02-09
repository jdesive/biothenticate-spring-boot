package com.softwarfare.biothenticate.spring.boot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "biothenticate")
public class BioThenticateProperties {

    private String url = "https://biothenticate.net:9502";
    private String username;
    private String password;

    private String requestTitle = "Spring Boot Login";
    private String requestMessage = "A login request for Spring Boot has been sent to your account. If this is you, please select approve below.";

}
