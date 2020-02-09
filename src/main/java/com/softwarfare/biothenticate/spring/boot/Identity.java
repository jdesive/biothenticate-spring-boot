package com.softwarfare.biothenticate.spring.boot;


import com.softwarfare.biothenticate.api.models.AccountRole;

import java.util.UUID;

public interface Identity {

    UUID getId();
    String getEmail();
    String getFirstName();
    String getLastName();

    AccountRole getAccountRole();
    boolean isInactive();

}
