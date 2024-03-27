package com.example.unitech.controller;


import com.example.unitech.dto.RateDto;
import com.example.unitech.dto.RateInfoDto;
import com.example.unitech.entity.enums.EnumCurrency;
import com.example.unitech.service.impl.RateServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

class RateControllerTest  {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateServiceImpl rateService;

    @Test
    void getRates_shouldReturnRateDto() throws Exception {
        EnumCurrency base = EnumCurrency.EUR;
        List<EnumCurrency> targets = Arrays.asList(EnumCurrency.USD, EnumCurrency.GBP);
        LocalDate date = LocalDate.of(2023, 5, 22);

        List<RateInfoDto> rates = Arrays.asList(
                new RateInfoDto(EnumCurrency.USD, 1.2),
                new RateInfoDto(EnumCurrency.GBP, 0.9)
        );

        RateDto mockedRateDto = RateDto.builder()
                .base(base)
                .rates(rates)
                .date(date)
                .build();

        when(rateService.calculateRate(base, targets, date)).thenReturn(mockedRateDto);

        mockMvc.perform(get("/v1/rate")
                        .param("base", base.toString())
                        .param("target", EnumCurrency.USD.toString())
                        .param("target", EnumCurrency.GBP.toString())
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base", is(base.toString())))
                .andExpect(jsonPath("$.rates[0].currency", is(EnumCurrency.USD.toString())))
                .andExpect(jsonPath("$.rates[0].rate", is(1.2)))
                .andExpect(jsonPath("$.rates[1].currency", is(EnumCurrency.GBP.toString())))
                .andExpect(jsonPath("$.rates[1].rate", is(0.9)))
                .andExpect(jsonPath("$.date", is(date.toString())));

        verify(rateService, times(1)).calculateRate(base, targets, date);
    }
}