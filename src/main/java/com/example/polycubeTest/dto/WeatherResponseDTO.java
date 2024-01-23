package com.example.polycubeTest.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherResponseDTO {
    private final WeatherDto weather;
    private final String message;

    @Getter
    @Builder
    public static class WeatherDto {
        private final Double temp;
        private final Double rainAmount;
        private final Double humid;
        private final String lastUpdateTime;
    }
}