package com.example.unitech.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;



@AllArgsConstructor
public class RestServiceException extends RuntimeException{
    private String serviceName;
    private HttpStatus statusCode;
    private String error;
}