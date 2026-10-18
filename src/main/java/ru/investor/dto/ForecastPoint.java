package ru.investor.dto;

public record ForecastPoint(
        String date,
        double value,
        String event
) {}
