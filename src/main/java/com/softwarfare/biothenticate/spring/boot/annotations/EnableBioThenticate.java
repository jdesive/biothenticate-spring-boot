package com.softwarfare.biothenticate.spring.boot.annotations;

import com.softwarfare.biothenticate.spring.boot.BioThenticateAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({BioThenticateAutoConfiguration.class})
public @interface EnableBioThenticate {
}
