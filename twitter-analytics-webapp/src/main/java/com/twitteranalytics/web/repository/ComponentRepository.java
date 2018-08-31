package com.twitteranalytics.web.repository;

import com.twitteranalytics.web.domain.Component;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ComponentRepository extends MongoRepository<Component, String> {

    Collection<Component> findAllByUser(@Param("user") String user);

}
