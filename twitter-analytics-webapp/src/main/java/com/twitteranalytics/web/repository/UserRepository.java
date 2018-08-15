package com.twitteranalytics.web.repository;

import com.twitteranalytics.web.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
