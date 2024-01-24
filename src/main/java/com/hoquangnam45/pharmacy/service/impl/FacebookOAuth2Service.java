package com.hoquangnam45.pharmacy.service.impl;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Clock;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2Request;
import com.hoquangnam45.pharmacy.config.OAuth2Config;
import com.hoquangnam45.pharmacy.constant.AuthProvider;
import com.hoquangnam45.pharmacy.pojo.FacebookOAuth2Config;
import com.hoquangnam45.pharmacy.pojo.FacebookUserInfo;
import com.hoquangnam45.pharmacy.service.IOAuth2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

@Service
public class FacebookOAuth2Service implements IOAuth2Service<FacebookUserInfo> {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private final FacebookOAuth2Config facebookOAuth2Config;
    private final String applicationUrl;
    private final List<String> scopes;

    public FacebookOAuth2Service(OAuth2Config oAuthConfig, @Value("${pharma.applicationUrl}") String applicationUrl) {
        this.facebookOAuth2Config = oAuthConfig.getFacebook();
        this.applicationUrl = applicationUrl;
        this.scopes = List.of("email", "public_profile");
    }

    @Override
    public String getAuthorizationUrl(String state) {
        return new AuthorizationCodeRequestUrl("https://www.facebook.com/dialog/oauth", facebookOAuth2Config.getClientId())
                .setRedirectUri(getRedirectUri(state))
                .setState(state)
                .setScopes(scopes)
                .build();
    }

    @Override
    public String getRedirectUri(String state) {
        return MessageFormat.format("{0}/oauth2/{1}/callback", applicationUrl, getProviderName());
    }

    @Override
    public FacebookUserInfo getUser(String state, String authorizationCode) throws IOException {
        AuthorizationCodeTokenRequest tokenRequest = new AuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl("https://graph.facebook.com/oauth/access_token"), authorizationCode)
                .setRedirectUri(getRedirectUri(state))
                .setScopes(scopes)
                .setClientAuthentication(new ClientParametersAuthentication(facebookOAuth2Config.getClientId(), facebookOAuth2Config.getClientSecret()))
                .setGrantType("authorization_code");
        TokenResponse tokenResponse = tokenRequest.execute();

        Credential credential = new Credential.Builder(BearerToken.queryParameterAccessMethod())
                .setClock(Clock.SYSTEM)
                .setJsonFactory(JSON_FACTORY)
                .setTransport(HTTP_TRANSPORT)
                .build()
                .setFromTokenResponse(tokenResponse);

        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setRootUrl("https://graph.facebook.com")
                .setServicePath("")
                .build();
        Oauth2Request<FacebookUserInfo> userInfoRequest = new Oauth2Request<>(
                oauth2,
                "GET",
                "/me?fields=email,id,name",
                null,
                FacebookUserInfo.class) {};
        return userInfoRequest.execute();
    }

    @Override
    public String getProviderName() {
        return AuthProvider.FACEBOOK.getProviderName();
    }
}
