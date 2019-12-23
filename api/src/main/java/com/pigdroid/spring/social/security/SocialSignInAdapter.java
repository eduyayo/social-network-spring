package com.pigdroid.spring.social.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import com.pigdroid.spring.social.config.Constants;

@Service
public class SocialSignInAdapter implements SignInAdapter {

    private UserDetailsService userDetailsService;

    public SocialSignInAdapter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        final UserDetails user = this.userDetailsService.loadUserByUsername(userId);
        final Authentication newAuth = new UsernamePasswordAuthenticationToken(
            user,
            null,
            user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        return Constants.WEB_URL + "/#/profile";
    }
}
