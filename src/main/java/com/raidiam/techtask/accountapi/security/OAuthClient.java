package com.raidiam.techtask.accountapi.security;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class OAuthClient {

    private String clientId;
    private String clientSecret;
    private Set<String> scopes;

}
