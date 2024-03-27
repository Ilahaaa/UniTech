package com.example.unitech.service;

import com.example.unitech.dto.RateDto;
import com.example.unitech.entity.enums.EnumCurrency;

import java.time.LocalDate;
import java.util.List;

public interface RateService {

    RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets, LocalDate date);

    RateDto calculateRate();

    RateDto calculateRate(EnumCurrency base);

    RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets);
    void updateRatesFromApi();



}
