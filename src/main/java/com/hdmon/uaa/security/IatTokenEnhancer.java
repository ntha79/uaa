package com.hdmon.uaa.security;

import com.hdmon.uaa.repository.UserRepository;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Adds the standard "iat" claim to tokens so we know when they have been created.
 * This is needed for a session timeout due to inactivity (ignored in case of "remember-me").
 */
@Component
public class IatTokenEnhancer implements TokenEnhancer {
    private final UserRepository userRepository;

    public IatTokenEnhancer(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        try {
            addClaims((DefaultOAuth2AccessToken) accessToken, authentication);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    private void addClaims(DefaultOAuth2AccessToken accessToken, OAuth2Authentication authentication) throws IOException {
        DefaultOAuth2AccessToken token = accessToken;
        Map<String, Object> additionalInformation = token.getAdditionalInformation();
        if(additionalInformation.isEmpty()) {
            additionalInformation = new LinkedHashMap<String, Object>();
        }
        //add "iat" claim with current time in secs
        //this is used for an inactive session timeout
        additionalInformation.put("iat", new Integer((int)(System.currentTimeMillis()/1000L)));
        additionalInformation.put("user_id", getUserLoginId(authentication));
        token.setAdditionalInformation(additionalInformation);
    }

    private Long getUserLoginId(OAuth2Authentication authentication) throws IOException
    {
        UserPrincipal user  = (UserPrincipal)authentication.getUserAuthentication().getPrincipal();
        return user.getUserID();
    }
}
