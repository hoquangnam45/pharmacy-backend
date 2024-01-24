package com.hoquangnam45.pharmacy.service.impl;

import com.amazonaws.services.codecommit.model.UserInfo;
import com.facebook.api.schema.ProfileGetInfoResponse;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Clock;
import com.hoquangnam45.pharmacy.config.OAuth2Config;
import com.hoquangnam45.pharmacy.constant.AuthProvider;
import com.hoquangnam45.pharmacy.pojo.FacebookOAuth2Config;
import com.hoquangnam45.pharmacy.service.IOAuth2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

@Service
public class FacebookOAuth2Service implements IOAuth2Service<ProfileGetInfoResponse> {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final FacebookOAuth2Config facebookOAuth2Config;
    private final String applicationUrl;

    public FacebookOAuth2Service(OAuth2Config oAuthConfig, @Value("pharma.applicationUrl") String applicationUrl) {
        this.facebookOAuth2Config = oAuthConfig.getFacebook();
        this.applicationUrl = applicationUrl;
    }

    @Override
    public String getRedirectUrl(String state) {
        return new AuthorizationCodeRequestUrl("https://graph.facebook.com/oauth/authorize", facebookOAuth2Config.getClientId())
                .setRedirectUri(MessageFormat.format("{0}/callback/{1}", applicationUrl, getProviderName()))
                .setState(state)
                .setScopes(List.of("email", "public_profile"))
                .build();
    }

    @Override
    public String getUser(String authorizationCode) throws IOException {
        AuthorizationCodeTokenRequest tokenRequest = new AuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl("https://graph.facebook.com/oauth/access_token"), authorizationCode);
        TokenResponse tokenResponse = tokenRequest.execute();

        Credential credential = new Credential.Builder(BearerToken.queryParameterAccessMethod())
                .setClock(Clock.SYSTEM)
                .setJsonFactory(JSON_FACTORY)
                .setTransport(HTTP_TRANSPORT)
                .setRequestInitializer()
                .build();
        String meUrl = MessageFormat.format("https://graph.facebook.com/me?access_token={0}", tokenResponse.getAccessToken());

    }

    @Override
    public String getProviderName() {
        return AuthProvider.FACEBOOK.getProviderName();
    }
}
