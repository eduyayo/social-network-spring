package com.pigdroid.spring.social.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pigdroid.spring.social.domain.Person;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocialConnectionSignUp implements ConnectionSignUp {

    private static final Logger log = LoggerFactory.getLogger(SocialConnectionSignUp.class);

    private PersonService personService;

    @Override
    public String execute(Connection<?> connection) {
        final UserProfile profile = connection.fetchUserProfile();
        log.debug("Authenticating social request: {}", profile);

        final String email = profile.getEmail();
        if (!StringUtils.isEmpty(email)) {
            Person person = this.personService.findByEmail(email);
            if (person == null) {
                person = this.personService.create(profile.getFirstName(), profile.getLastName(), email, "");
                log.info("New person: {} was successfully authenticated by email: {}", person, email);
            } else {
                log.info("Existing person: {} was successfully authenticated by email: {}", person, email);
            }
        } else {
            // TODO: 02.02.2018 Handle blank email with error on a client side
            log.error("Failed to authenticate person with blank email from connection: {}", connection);
        }

        return email;
    }

}
