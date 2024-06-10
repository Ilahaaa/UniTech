package com.example.unitech;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class UniTechApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniTechApplication.class, args);
    }


}
