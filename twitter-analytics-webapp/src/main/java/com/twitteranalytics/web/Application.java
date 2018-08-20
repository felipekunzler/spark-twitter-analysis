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
import java.time.LocalDate;

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
        mockComponents();

        userRepository.save(new User("admin", hash("123"), "Administrador"));
        userRepository.save(new User("user", hash("123"), "User"));

        System.out.println("Component found with findAll():");
        componentRepository.findAll().forEach(System.out::println);

        System.out.println("User found with findAll():");
        userRepository.findAll().forEach(System.out::println);
    }

    private void mockComponents() {
        Component c1 = new Component();
        c1.setKeyword("Microsoft");
        c1.setFrom(LocalDate.now().minusDays(3));
        c1.setTo(LocalDate.now().minusDays(1));
        c1.setUser("user");
        c1.setType("chart");

        Component c2 = new Component();
        c2.setKeyword("Google");
        c2.setFrom(LocalDate.now().minusDays(3));
        c2.setTo(LocalDate.now().minusDays(1));
        c2.setUser("user");
        c2.setType("trends");
        componentRepository.save(c1);
        componentRepository.save(c2);

    }

    private String hash(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
