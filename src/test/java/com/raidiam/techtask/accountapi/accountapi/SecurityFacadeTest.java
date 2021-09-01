package com.raidiam.techtask.accountapi.accountapi;

import com.raidiam.techtask.accountapi.model.Customer;
import com.raidiam.techtask.accountapi.security.Authorisation;
import com.raidiam.techtask.accountapi.security.SecurityFacade;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityFacadeTest {

    @Test
    void canCreateAccessToken() {

        SecurityFacade facade = new SecurityFacade();
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .build();

        String token = facade.authorize(customer, "client1", "gp243hg32408gh304eg", "accounts");

        Authorisation authorisation = facade.introspect(token);

        assertEquals(customer, authorisation.getCustomer());

    }

}
