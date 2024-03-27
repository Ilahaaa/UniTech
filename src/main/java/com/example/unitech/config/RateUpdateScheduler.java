package com.example.unitech.config;

import com.example.unitech.service.RateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateUpdateScheduler {

    private final RateService rateService;

    public RateUpdateScheduler(RateService rateService) {
        this.rateService = rateService;
    }

    @Scheduled(fixedRate = 60000)
    public void updateRates() {
        rateService.updateRatesFromApi();
    }
}
