package com.example.polycubeTest.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MidTermWeatherResponseDTO {
    private String regionCode;
    private double temperature;
}