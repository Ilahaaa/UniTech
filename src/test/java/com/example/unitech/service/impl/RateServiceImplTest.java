package com.example.unitech.service.impl;


import com.example.unitech.dto.RateDto;
import com.example.unitech.dto.RateInfoDto;
import com.example.unitech.entity.RateEntity;
import com.example.unitech.entity.enums.EnumCurrency;
import com.example.unitech.repository.RateRepository;
import com.example.unitech.response.RateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class RateServiceImplTest  {


    @Mock
    private RateRepository rateRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RateServiceImpl rateService;


    @Test
    void whenCalculateRate_butRateRepositoryFoundOne() {

        EnumCurrency base = EnumCurrency.EUR;
        List<EnumCurrency> targets = Arrays.asList(EnumCurrency.USD, EnumCurrency.GBP);
        LocalDate date = LocalDate.of(2024, 3, 26);

        RateEntity mockedRateEntity = new RateEntity();
        mockedRateEntity.setBase(base);
        mockedRateEntity.setDate(date);
        Map<EnumCurrency, Double> rates = new HashMap<>();
        rates.put(EnumCurrency.USD, 1.2);
        rates.put(EnumCurrency.GBP, 0.9);
        mockedRateEntity.setRates(rates);

        List<RateInfoDto> rateInfoList = targets.stream()
                .map(currency -> new RateInfoDto(currency, rates.get(currency)))
                .collect(Collectors.toList());

        RateDto expected = RateDto.builder()
                .id(mockedRateEntity.getId())
                .base(mockedRateEntity.getBase())
                .date(mockedRateEntity.getDate())
                .rates(rateInfoList)
                .build();

        when(rateRepository.findOneByDate(date)).thenReturn(Optional.of(mockedRateEntity));


        RateDto result = rateService.calculateRate(base, targets, date);

        assertThat(result.getBase()).isEqualTo(expected.getBase());
        assertThat(result.getDate()).isEqualTo(expected.getDate());
        assertThat(result.getRates()).hasSize(2);
        assertThat(result.getRates()).containsExactlyInAnyOrder(
                new RateInfoDto(EnumCurrency.USD, 1.2),
                new RateInfoDto(EnumCurrency.GBP, 0.9)
        );

        verify(rateRepository, times(1)).findOneByDate(date);

        verify(restTemplate, times(0)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RateResponse.class)
        );
    }

    @Test
    void whenCalculateRate_andRateRepositoryNotFound() {
        EnumCurrency base = EnumCurrency.EUR;
        List<EnumCurrency> targets = Arrays.asList(EnumCurrency.USD, EnumCurrency.GBP);
        LocalDate date = LocalDate.of(2024, 3, 26);

        RateEntity mockedRateEntity = new RateEntity();
        mockedRateEntity.setBase(base);
        mockedRateEntity.setDate(date);
        Map<EnumCurrency, Double> rates = new HashMap<>();
        rates.put(EnumCurrency.USD, 1.2);
        rates.put(EnumCurrency.GBP, 0.9);
        mockedRateEntity.setRates(rates);

        List<RateInfoDto> rateInfoList = targets.stream()
                .map(currency -> new RateInfoDto(currency, rates.get(currency)))
                .collect(Collectors.toList());

        RateDto expected = RateDto.builder()
                .id(mockedRateEntity.getId())
                .base(mockedRateEntity.getBase())
                .date(mockedRateEntity.getDate())
                .rates(rateInfoList)
                .build();

        RateResponse mockedRateResponse = RateResponse.builder()
                .base(base)
                .rates(rates)
                .date(date)
                .build();

        ResponseEntity<RateResponse> mockedResponseEntity = ResponseEntity.ok().body(mockedRateResponse);

        when(rateRepository.findOneByDate(date)).thenReturn(Optional.empty());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RateResponse.class)
        )).thenReturn(mockedResponseEntity);

        when(rateRepository.save(any(RateEntity.class))).thenReturn(mockedRateEntity);

        RateDto result = rateService.calculateRate(base, targets, date);

        assertThat(result.getBase()).isEqualTo(expected.getBase());
        assertThat(result.getDate()).isEqualTo(expected.getDate());
        assertThat(result.getRates()).hasSize(2);
        assertThat(result.getRates()).containsExactlyInAnyOrder(
                new RateInfoDto(EnumCurrency.USD, 1.2),
                new RateInfoDto(EnumCurrency.GBP, 0.9)
        );

        verify(rateRepository, times(1)).findOneByDate(date);
        verify(rateRepository, times(1)).save(any(RateEntity.class));

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RateResponse.class)
        );
    }

}