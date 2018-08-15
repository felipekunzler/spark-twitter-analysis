package com.twitteranalytics.web.repository;

import com.twitteranalytics.web.domain.Component;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComponentRepository extends MongoRepository<Component, String> {

    Component findByName(String name);

}
