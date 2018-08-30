package com.twitteranalytics.web.service;

import com.twitteranalytics.web.domain.KeywordAnalysis;
import com.twitteranalytics.web.domain.Sentiments;
import com.twitteranalytics.web.domain.TrendsBarData;
import com.twitteranalytics.web.repository.KeywordStatisticsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccumulatorService {

    private static final int GROUPS = 8;

    @Resource
    private KeywordStatisticsRepository keywordStatisticsRepository;

    public TrendsBarData computeTrendsData(String keyword, LocalDate from, LocalDate to) {
        List<KeywordAnalysis> keywords = keywordStatisticsRepository.findByKeywordAndDateBetween(keyword.toLowerCase(), toInstant(from), toInstant(to));

        Map<String, Sentiments> accumulatedTrends = new HashMap<>();
        TrendsBarData data = new TrendsBarData();
        data.setTrends(accumulatedTrends);
        data.setSentiments(new Sentiments());

        for (KeywordAnalysis k : keywords) {
            incrementSentiments(data.getSentiments(), k.getSentiments());
            for (Map.Entry<String, Sentiments> entry : k.getTrends().entrySet()) {
                accumulatedTrends.merge(entry.getKey(), entry.getValue(), this::incrementSentiments);
            }
        }
        return data;
    }

    public Map<LocalDate, Sentiments> computeLineChartData(String keyword, LocalDate from, LocalDate to) {
        List<KeywordAnalysis> keywords = keywordStatisticsRepository.findByKeywordAndDateBetween(keyword.toLowerCase(), toInstant(from), toInstant(to));

        Sentiments[] sentiments = new Sentiments[GROUPS];
        for (int i = 0; i < sentiments.length; i++) {
            sentiments[i] = new Sentiments();
        }

        int groupSize = keywords.size() / GROUPS;
        int totalSize = groupSize * GROUPS;
        for (int i = 0; i < totalSize; i++) {
            int currentGroup = i * GROUPS / totalSize;
            sentiments[currentGroup] = incrementSentiments(sentiments[currentGroup], keywords.get(i).getSentiments());
        }

        Map<LocalDate, Sentiments> map = new HashMap<>(GROUPS);
        for (int i = 0; i < GROUPS; i++) {
            LocalDate date = keywords.get(i * groupSize).getDate();
            map.put(date, sentiments[i]);
        }
        return map;
    }

    private Sentiments incrementSentiments(Sentiments accumulated, Sentiments sentiments) {
        accumulated.setPositive(accumulated.getPositive() + sentiments.getPositive());
        accumulated.setNegative(accumulated.getNegative() + sentiments.getNegative());
        accumulated.setNeutral(accumulated.getNeutral() + sentiments.getNeutral());
        return accumulated;
    }

    private Instant toInstant(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

}
