package com.twitteranalytics.web.config;

import com.twitteranalytics.web.domain.User;
import com.twitteranalytics.web.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<RuntimeException> notFoundException = () -> new UsernameNotFoundException("Username " + username + " not found");
        User user = userRepository.findById(username).orElseThrow(notFoundException);
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), getGrantedAuthorities(username));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(String username) {
        return Arrays.asList(() -> "ROLE_ADMIN", () -> "ROLE_BASIC");
    }

}
