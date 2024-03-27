package com.example.unitech.controller;

import com.example.unitech.dto.RateDto;
import com.example.unitech.entity.enums.EnumCurrency;
import com.example.unitech.service.RateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/v1/rate")
@Slf4j
public class RateController {

    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }
    @GetMapping
    public ResponseEntity<RateDto> getRates(@RequestParam(name = "base",required = false) EnumCurrency base,
                                            @RequestParam(name = "target",required = false) List<EnumCurrency> target,
                                            @RequestParam(name = "date",required = false) @DateTimeFormat(pattern = "yyyy-mm-dd") LocalDate date) {
        return ResponseEntity.ok(rateService.calculateRate(base, target,date));
    }
}