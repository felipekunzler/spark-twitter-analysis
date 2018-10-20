package com.twitteranalytics.web;

import com.twitteranalytics.web.domain.Component;
import com.twitteranalytics.web.domain.KeywordAnalysis;
import com.twitteranalytics.web.domain.Sentiments;
import com.twitteranalytics.web.domain.User;
import com.twitteranalytics.web.repository.ComponentRepository;
import com.twitteranalytics.web.repository.KeywordStatisticsRepository;
import com.twitteranalytics.web.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Resource
    private ComponentRepository componentRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private KeywordStatisticsRepository keywordStatisticsRepository;

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
        userRepository.deleteAll();
        mockComponents();

        userRepository.save(new User("admin", hash("123")));
        userRepository.save(new User("user", hash("123")));

        System.out.println("Component found with findAll():");
        componentRepository.findAll().forEach(System.out::println);

        System.out.println("User found with findAll():");
        userRepository.findAll().forEach(System.out::println);

    }

    private void mockComponents() {
        Component c1 = new Component();
        c1.setKeyword("Microsoft");
        c1.setFrom(LocalDate.of(2009, 1, 1));
        c1.setTo(LocalDate.of(2009, 12, 31));
        c1.setUser("admin");
        c1.setType("pie");

        Component c2 = new Component();
        c2.setKeyword("Microsoft");
        c2.setFrom(LocalDate.of(2009, 1, 1));
        c2.setTo(LocalDate.of(2009, 12, 31));
        c2.setUser("admin");
        c2.setType("chart");

        Component c3 = new Component();
        c3.setKeyword("Microsoft");
        c3.setFrom(LocalDate.of(2009, 1, 1));
        c3.setTo(LocalDate.of(2009, 12, 31));
        c3.setUser("admin");
        c3.setType("trends");
        componentRepository.save(c1);
        componentRepository.save(c2);
        componentRepository.save(c3);
    }

    private void mockStatistics() {
        for (int i = 1; i < 60; i++) {
            Map<String, Sentiments> map = new HashMap<>();
            map.put("surface", new Sentiments(i*3, i*2, i));
            map.put("windows", new Sentiments(i*3, i*2, i));
            createStatistics("microsoft", LocalDate.of(2018, 5, 10).plusDays(i), new Sentiments(i*3 + 100, i*2 + 100, i + 100), map);
        }
    }

    private void createStatistics(String keyword, LocalDate date, Sentiments sentiments, Map<String, Sentiments> map) {
        KeywordAnalysis keywordAnalysis = new KeywordAnalysis();
        keywordAnalysis.setKeyword(keyword);
        keywordAnalysis.setSentiments(sentiments);
        keywordAnalysis.setDate(date);
        keywordAnalysis.setTrends(map);
        keywordStatisticsRepository.save(keywordAnalysis);
    }

    private String hash(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
