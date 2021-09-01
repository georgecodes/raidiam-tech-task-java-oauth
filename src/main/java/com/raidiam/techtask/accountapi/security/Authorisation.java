package com.raidiam.techtask.accountapi.security;

import com.raidiam.techtask.accountapi.model.Customer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Authorisation {

    private OAuthClient client;
    private Customer customer;
    private String scope;

    public String toString() {
        return String.format("Client ID %s Customer %s scope %s", client.getClientId(), customer.getId(), scope);
    }

}
