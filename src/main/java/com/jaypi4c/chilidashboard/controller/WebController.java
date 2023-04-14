package com.jaypi4c.chilidashboard.controller;

import com.jaypi4c.chilidashboard.model.DataSet;
import com.jaypi4c.chilidashboard.model.SoilData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
public class WebController {

    private final RestTemplate restTemplate;

    @Value("${jaypi4c.chili-app.base-url}")
    private String baseUrl;
    @Value("${jaypi4c.chili-app.port}")
    private String port;

    public WebController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("chartData", getChartData(LocalDateTime.now().minusDays(1), LocalDateTime.now(), 0, 50));
        return "dashboard";
    }

    private List<SoilData> getChartData(LocalDateTime beginDate, LocalDateTime endDate, int page, int size) {

        String url = MessageFormat.format("{0}:{1}/chili-app/v1/soilData/between-dates?beginDate={2}&endDate={3}&page={4}&size={5}",
                baseUrl, port, beginDate, endDate, page, size);

        DataSet soilDataSet = restTemplate.getForObject(url, DataSet.class);

        assert soilDataSet != null;
        return soilDataSet.getEmbedded().getSoilDataList();
    }


}
