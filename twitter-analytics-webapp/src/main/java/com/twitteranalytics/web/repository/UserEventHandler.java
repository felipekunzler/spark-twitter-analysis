package com.twitteranalytics.web.repository;

import com.twitteranalytics.web.domain.User;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RepositoryEventHandler(User.class)
public class UserEventHandler {

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @HandleBeforeCreate
    public void onUserCreate(User user) {
        hashPassword(user);
    }

    @HandleBeforeSave
    public void onUserUpdate(User user) {
        hashPassword(user);
    }

    private void hashPassword(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String hash = passwordEncoder.encode(user.getPassword());
            user.setPassword(hash);
        }
    }

}

