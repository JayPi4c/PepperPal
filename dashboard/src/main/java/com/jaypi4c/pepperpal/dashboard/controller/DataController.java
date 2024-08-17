package com.jaypi4c.pepperpal.dashboard.controller;

import com.jaypi4c.pepperpal.dashboard.model.DataSet;
import com.jaypi4c.pepperpal.dashboard.model.SoilData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/data")
public class DataController {


    private final RestTemplate restTemplate;

    @Value("${jaypi4c.chili-app.base-url}")
    private String baseUrl;

    public DataController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public List<SoilData> getSoilData(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (end == null)
            end = LocalDateTime.now();

        if (start == null)
            start = end.minusDays(1);

        log.debug("Getting data between {} and {}", start, end);

        List<SoilData> data = new ArrayList<>();
        List<SoilData> tmp;
        int page = 0;
        do {
            try {
                tmp = getChartData(start, end, page, 50);
            } catch (NullPointerException e) {
                tmp = Collections.emptyList();
            }
            page++;
            data.addAll(tmp);
        } while (!tmp.isEmpty());

        return data;
    }

    private List<SoilData> getChartData(LocalDateTime beginDate, LocalDateTime endDate, int page, int size) {
        String url = MessageFormat.format("{0}/chili-app/v1/soilData/between-dates?beginDate={1}&endDate={2}&page={3}&size={4}",
                baseUrl, beginDate, endDate, page, size);
        log.debug("Fetching data for page {} with url {}", page, url);
        DataSet soilDataSet = restTemplate.getForObject(url, DataSet.class);

        assert soilDataSet != null;
        return soilDataSet.getEmbedded().getSoilDataList();
    }

}
