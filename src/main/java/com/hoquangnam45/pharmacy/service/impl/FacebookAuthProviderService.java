package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.constant.AuthProvider;
import com.hoquangnam45.pharmacy.service.IAuthProvider;
import org.springframework.stereotype.Service;

@Service
public class FacebookAuthProviderService implements IAuthProvider {
    @Override
    public String getRedirectUrl() {
        return new GoogleAuthorizationCodeFlow.;
    }

    @Override
    public String getUser(String authorizationCode) {
        return null;
    }

    @Override
    public String getProviderName() {
        return AuthProvider.FACEBOOK.getProviderName();
    }
}
