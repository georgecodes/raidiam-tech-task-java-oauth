package com.raidiam.techtask.accountapi.security;

import com.raidiam.techtask.accountapi.model.Customer;
import com.raidiam.techtask.accountapi.util.Exceptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityFacade {

    private Map<String, OAuthClient> clients = new HashMap<>();
    private Map<String, Authorisation> tokens = new HashMap<>();

    public SecurityFacade() {
        OAuthClient client1 = OAuthClient.builder()
                .clientId("client1")
                .clientSecret("gp243hg32408gh304eg")
                .scopes(Set.of("accounts"))
                .build();
        OAuthClient client2 = OAuthClient.builder()
                .clientId("client2")
                .clientSecret("e5uw3q0gherthrgef42g")
                .scopes(Set.of("accounts", "transactions"))
                .build();
        OAuthClient client3 = OAuthClient.builder()
                .clientId("client3")
                .clientSecret("f2oighw40g823ioug34")
                .scopes(Set.of("transactions"))
                .build();
        clients.put(client1.getClientId(), client1);
        clients.put(client2.getClientId(), client2);
        clients.put(client3.getClientId(), client3);

    }

    public String authorize(Customer customer, String clientId, String clientSecret, String scope) {
        OAuthClient client = clients.get(clientId);
        if(client == null) {
            throw Exceptions.unathorised();
        }
        if(!client.getClientSecret().equals(clientSecret)) {
            throw Exceptions.unathorised();
        }
        if(!client.getScopes().contains(scope)) {
            throw Exceptions.unathorised();
        }
        String accessToken = RandomStringUtils.randomAlphabetic(24);
        Authorisation authorisation = Authorisation.builder()
                .client(client)
                .customer(customer)
                .scope(scope)
                .build();
        tokens.put(accessToken, authorisation);
        return accessToken;
    }

    public Authorisation introspect(String bearerToken) {
        return Optional.ofNullable(tokens.get(bearerToken)).orElseThrow(Exceptions::unathorised);
    }
}
