package com.hoquangnam45.pharmacy.service.impl;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.hoquangnam45.pharmacy.config.OAuthConfig;
import com.hoquangnam45.pharmacy.constant.AuthProvider;
import com.hoquangnam45.pharmacy.pojo.GoogleAuthConfig;
import com.hoquangnam45.pharmacy.service.IAuthProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleAuthProviderService implements IAuthProvider {
    private final GoogleAuthConfig googleAuthConfig;
    private final String callbackUrl;

    public GoogleAuthProviderService(OAuthConfig oauthConfig) {
        this.googleAuthConfig = oauthConfig.getGoogle();
        this.callbackUrl = oauthConfig.getApplicationUrl();
    }


    @Override
    public String getRedirectUrl(String state) {
        return new GoogleBrowserClientRequestUrl(googleAuthConfig.getClientId(), callbackUrl, List.of(
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/userinfo.profile"))
                .setState(state)
                .build();
    }

    @Override
    public String getUser(String authorizationCode) {
        return x    x`;
    }

    @Override
    public String getProviderName() {
        return AuthProvider.FACEBOOK.getProviderName();
    }
}
