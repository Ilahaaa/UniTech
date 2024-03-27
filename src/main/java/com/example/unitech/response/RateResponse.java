package com.example.unitech.response;

import com.example.unitech.entity.enums.EnumCurrency;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;

@Builder
public record RateResponse(EnumCurrency base,
                           LocalDate date,
                           Map<EnumCurrency, Double> rates,
                           boolean success,
                           long timestamp) {
}