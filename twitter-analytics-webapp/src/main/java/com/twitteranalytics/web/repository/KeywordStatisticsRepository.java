package com.twitteranalytics.web.repository;

import com.twitteranalytics.web.domain.KeywordAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface KeywordStatisticsRepository extends MongoRepository<KeywordAnalysis, String> {

    List<KeywordAnalysis> findByKeywordAndDateBetween(String keyword, Instant from, Instant to);

}
