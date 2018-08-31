package com.twitteranalytics.web.controller;

import com.twitteranalytics.web.domain.Sentiments;
import com.twitteranalytics.web.domain.TrendsBarData;
import com.twitteranalytics.web.service.AccumulatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Map;

@RestController
public class DataController {

    @Resource
    private AccumulatorService accumulatorService;

    @GetMapping("/analytics/trends")
    public TrendsBarData getTrendsData(String keyword, String from, String to) {
        return accumulatorService.computeTrendsData(keyword, LocalDate.parse(from).minusDays(1), LocalDate.parse(to).plusDays(1));
    }

    @GetMapping("/analytics/chart")
    public Map<LocalDate, Sentiments> getChartData(String keyword, String from, String to) {
        return accumulatorService.computeLineChartData(keyword, LocalDate.parse(from).minusDays(1), LocalDate.parse(to).plusDays(1));
    }

}
