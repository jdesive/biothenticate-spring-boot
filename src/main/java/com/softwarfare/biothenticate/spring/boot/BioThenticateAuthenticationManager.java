package com.softwarfare.biothenticate.spring.boot;

import com.softwarfare.biothenticate.api.BioThenticateClient;
import com.softwarfare.biothenticate.api.models.*;
import com.softwarfare.biothenticate.spring.boot.properties.BioThenticateProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class BioThenticateAuthenticationManager implements AuthenticationManager {

    private BioThenticateClient bioThenticateClient;
    private IdentityService identityService;
    private BioThenticateProperties bioThenticateProperties;

    public BioThenticateAuthenticationManager(BioThenticateProperties bioThenticateProperties, IdentityService identityService) {
        this.bioThenticateClient = new BioThenticateClient(bioThenticateProperties.getUrl(), bioThenticateProperties.getUsername(), bioThenticateProperties.getPassword());
        this.identityService = identityService;
        this.bioThenticateProperties = bioThenticateProperties;

        try {
            User[] users = this.bioThenticateClient.getAllUsers(UserFilter.NONE, true);

            Arrays.stream(users).forEach(user -> {
                IdentityImpl identity = new IdentityImpl();
                identity.setId(user.getId());
                identity.setEmail(user.getEmail());
                identity.setFirstName(user.getFirstName());
                identity.setLastName(user.getLastName());
                identity.setAccountRole(user.getRole());
                identity.setInactive(user.isInactive());
                this.identityService.save(identity);
            });

        } catch (IOException e) {
            log.error("Error syncing users from biothenticate", e);
        }

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

        String name = authentication.getName();
        Object credentials = authentication.getCredentials();

        if (!pattern.matcher(name).matches()) {
            log.error("Principal is not an email");
            return null;
        }

        if (!(credentials instanceof String)) {
            log.error("Credentials is not type of String");
            return null;
        }

        String password = credentials.toString();
        try {
            LoginResponse loginResponse = this.bioThenticateClient.login(name, password);
            if (!loginResponse.isSuccess()) {
                throw new BadCredentialsException("Bad Credentials for user " + name);
            }

            AuthenticateResponse response = this.bioThenticateClient.authenticate(AuthenticationType.IRIS, name, this.bioThenticateProperties.getRequestMessage(), this.bioThenticateProperties.getRequestMessage(), this.bioThenticateProperties.getRequestTitle());
            if (!response.isAuthenticated()) {
                throw new BadCredentialsException("Authentication failed for " + name);
            }
            TokenUser tokenUser = this.bioThenticateClient.parseToken(loginResponse.getToken());
            IdentityImpl identity = new IdentityImpl();
            identity.setId(tokenUser.getId());
            identity.setEmail(tokenUser.getEmail());
            identity.setFirstName(tokenUser.getFirstName());
            identity.setLastName(tokenUser.getLastName());
            identity.setAccountRole(tokenUser.getRole());
            identity.setInactive(tokenUser.isInactive());
            this.identityService.save(identity);

            SimpleGrantedAuthority authority = new  SimpleGrantedAuthority(tokenUser.getRole().name());

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(authority);

            return new UsernamePasswordAuthenticationToken(name, password, authorities);
        } catch (IOException e) {
            log.error("Error authenticating user with BioThenticate", e);
            return null;
        }
    }

}
