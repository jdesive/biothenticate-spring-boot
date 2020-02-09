package com.softwarfare.biothenticate.spring.boot;

import com.softwarfare.biothenticate.api.BioThenticateClient;
import com.softwarfare.biothenticate.spring.boot.properties.BioThenticateProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(IdentityService.class)
@EnableConfigurationProperties(BioThenticateProperties.class)
public class BioThenticateAutoConfiguration {

    @Autowired
    private BioThenticateProperties bioThenticateProperties;

    @Bean
    public BioThenticateClient getBioThenticateClient() {
        return new BioThenticateClient(bioThenticateProperties.getUrl(), bioThenticateProperties.getUsername(), bioThenticateProperties.getPassword());
    }

}
