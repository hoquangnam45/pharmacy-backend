package com.hoquangnam45.pharmacy.service;

// FE make request to BE -> BE redirect to third party authentication service
// After user authenticated complete -> third party authentication service redirect user back to FE
// with authorization code -> FE submit this authorization code back to BE, backend then use this authorization
// code + client secrets to generate the access token to access the user profile + info -> then generate
// the jwt token for user
public interface IAuthProvider {
    String getRedirectUrl();
    String getUser(String authorizationCode);
    String getProviderName();
}
