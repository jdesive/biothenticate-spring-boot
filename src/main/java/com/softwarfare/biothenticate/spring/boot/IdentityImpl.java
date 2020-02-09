package com.softwarfare.biothenticate.spring.boot;

import com.softwarfare.biothenticate.api.models.AccountRole;
import lombok.Data;

import java.util.UUID;

@Data
public class IdentityImpl implements Identity {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;

    private AccountRole accountRole;
    private boolean inactive;

}
