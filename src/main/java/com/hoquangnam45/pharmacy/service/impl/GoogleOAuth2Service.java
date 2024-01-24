package com.hoquangnam45.pharmacy.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.hoquangnam45.pharmacy.config.OAuth2Config;
import com.hoquangnam45.pharmacy.constant.AuthProvider;
import com.hoquangnam45.pharmacy.pojo.GoogleOAuth2Config;
import com.hoquangnam45.pharmacy.service.IOAuth2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GoogleOAuth2Service implements IOAuth2Service<Userinfo> {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final String applicationUrl;
    private final GoogleAuthorizationCodeFlow flow;

    // References: https://developers.google.com/identity/protocols/oauth2/web-server?hl=vi
    public GoogleOAuth2Service(OAuth2Config oauthConfig, @Value("pharma.applicationUrl") String applicationUrl) {
        GoogleOAuth2Config googleAuthConfig = oauthConfig.getGoogle();
        this.applicationUrl = applicationUrl;
        this.flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                googleAuthConfig.getClientId(),
                googleAuthConfig.getClientSecret(),
                List.of("https://www.googleapis.com/auth/userinfo.email",
                        "https://www.googleapis.com/auth/userinfo.profile"))
                .setApprovalPrompt("select_account")
                .build();
                // References: https://stackoverflow.com/questions/30637984/what-does-offline-access-in-oauth-mean
                // .setAccessType("offline").build()(googleAuthConfig.getClientId(), googleAuthConfig.getClientSecret(), List.of(
    }

    @Override
    public String getRedirectUrl(String state) {
        return flow.newAuthorizationUrl().setState(state)
                .setRedirectUri(MessageFormat.format("{0}/callback/{1}", applicationUrl, getProviderName()))
                .build();
    }

    @Override
    public Userinfo getUser(String authorizationCode) throws IOException, ExecutionException {
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(authorizationCode).execute();

        Credential credential = flow.createAndStoreCredential(tokenResponse, null);

        return new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build()
                .userinfo()
                .v2()
                .me()
                .get()
                .execute();
    }

    @Override
    public String getProviderName() {
        return AuthProvider.GOOGLE.getProviderName();
    }
}
