package com.twitteranalytics.web;

import com.twitteranalytics.web.domain.Component;
import com.twitteranalytics.web.domain.User;
import com.twitteranalytics.web.repository.ComponentRepository;
import com.twitteranalytics.web.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        componentRepository.deleteAll();
        componentRepository.save(new Component("a", "ca"));
        componentRepository.save(new Component("b", "cb"));

        userRepository.save(new User("admin", hash("123"), "Administrador"));
        userRepository.save(new User("user", hash("123"), "User"));

        System.out.println("Component found with findAll():");
        componentRepository.findAll().forEach(System.out::println);

        System.out.println("User found with findAll():");
        userRepository.findAll().forEach(System.out::println);
    }

    private String hash(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
