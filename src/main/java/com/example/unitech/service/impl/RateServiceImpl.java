package com.example.unitech.service.impl;

import com.example.unitech.dto.RateDto;
import com.example.unitech.dto.RateInfoDto;
import com.example.unitech.entity.RateEntity;
import com.example.unitech.entity.enums.EnumCurrency;
import com.example.unitech.repository.RateRepository;
import com.example.unitech.response.RateResponse;
import com.example.unitech.service.RateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.unitech.constants.Constants.EXCHANGE_API_API_KEY;
import static com.example.unitech.constants.Constants.EXCHANGE_API_BASE_URL;

@Service
@AllArgsConstructor
@Slf4j
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;
    private final RestTemplate restTemplate;
@Override
    public RateDto calculateRate() {
        return calculateRate(null);
    }
@Override
    public RateDto calculateRate(EnumCurrency base) {
        return calculateRate(base, null);
    }
@Override
    public RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets) {
        return calculateRate(base, targets, null);
    }

    @Override
    public void updateRatesFromApi() {
   //  calculateRate();
    }

    @Override
    public RateDto calculateRate(EnumCurrency base, List<EnumCurrency> targets, LocalDate date) {

        base = Optional.ofNullable(base).orElse(EnumCurrency.EUR);
        targets = Optional.ofNullable(targets)
                .orElseGet(() -> Arrays.asList(EnumCurrency.values()));
        date = Optional.ofNullable(date).orElse(LocalDate.now());

        LocalDate finalDate = date;
        EnumCurrency finalBase = base;
        List<EnumCurrency> finalTargets = targets;

        RateEntity rateEntity = rateRepository.findOneByDate(date)
                .orElseGet(() -> saveRatesFromApi(finalDate, finalBase, finalTargets));

        Map<EnumCurrency, Double> rates = rateEntity.getRates();

        List<RateInfoDto> rateInfoList = targets.stream()
                .map(currency -> new RateInfoDto(currency, rates.get(currency)))
                .collect(Collectors.toList());

        return RateDto.builder()
                .id(rateEntity.getId())
                .base(rateEntity.getBase())
                .date(rateEntity.getDate())
                .rates(rateInfoList)
                .build();
    }

    private RateEntity saveRatesFromApi(LocalDate rateDate, EnumCurrency base, List<EnumCurrency> targets) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("apikey", EXCHANGE_API_API_KEY);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> headersEntity = new HttpEntity<>(headers);
        String url = getExchangeUrl(rateDate, base, targets);

        ResponseEntity<RateResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, headersEntity, RateResponse.class);

        RateResponse rates = responseEntity.getBody();
        RateEntity entity = convert(rates);
        return rateRepository.save(entity);
    }

    private String getExchangeUrl(LocalDate rateDate, EnumCurrency base, List<EnumCurrency> targets) {

        String symbols = String.join("%2C", targets.stream().map(EnumCurrency::name).toArray(String[]::new));
        return EXCHANGE_API_BASE_URL + rateDate + "?symbols=" + symbols + "&base=" + base;
    }

    private RateEntity convert(RateResponse source) {

        Map<EnumCurrency, Double> rates = source.rates();

        return RateEntity.builder()
                .base(source.base())
                .date(source.date())
                .rates(rates)
                .build();

    }
}
