package com.example.unitech.exception;



public record RestTemplateError (
        String timestamp,
        String status,
        String error,
        String path
){ }