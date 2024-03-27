package com.example.unitech.dto;


import com.example.unitech.entity.enums.EnumCurrency;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class RateDto {
    private Long id;
    private EnumCurrency base;
    private LocalDate date;
    List<RateInfoDto> rates;
}