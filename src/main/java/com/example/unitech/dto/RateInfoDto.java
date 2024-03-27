package com.example.unitech.dto;

import com.example.unitech.entity.enums.EnumCurrency;

public record RateInfoDto(EnumCurrency currency, Double rate) {}