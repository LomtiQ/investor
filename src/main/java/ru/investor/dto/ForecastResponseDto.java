package ru.investor.dto;

import java.util.List;

public record ForecastResponseDto (
        double currentCapital,
        double totalAfterForecast,
        List<ForecastPoint> points
)
{}
