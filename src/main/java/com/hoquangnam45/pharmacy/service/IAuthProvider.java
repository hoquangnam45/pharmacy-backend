package com.hoquangnam45.pharmacy.service;

// FE make request to BE -> BE redirect user to third party authentication service
// After user authenticated complete -> third party authentication service redirect user back to FE
// with authorization code -> FE submit this authorization code back to BE, backend then use this authorization
// code + client secrets to generate the access token to access the user profile + info -> then generate
// the jwt token for user
public interface IAuthProvider {
    // Why this need a state parameter, to prevent csrf attack where attacker could
    // trigger a request to BE which will result in a redirection to a third-party oauth provider
    // which after authenticated the attacker could then stop and saved the callback URL and then made someone click on this callback URL
    // would result in redirection back to FE with authorization code attached causing FE to send this authorization
    // code back to BE which would result in a valid jwt token of the attacker to be issued, the user would not notice
    // that they currently login as the attacker
    // Reference: https://stackoverflow.com/questions/35985551/how-does-csrf-work-without-state-parameter-in-oauth2-0
    // Reference: https://web.archive.org/web/20170211202441/http://www.twobotechnologies.com/blog/2014/02/importance-of-state-in-oauth2.html
    String getRedirectUrl(String state);
    String getUser(String authorizationCode);
    String getProviderName();
}
