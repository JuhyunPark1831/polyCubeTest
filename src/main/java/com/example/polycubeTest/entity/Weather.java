package com.example.polycubeTest.entity;

import com.example.polycubeTest.dto.WeatherResponseDTO;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Weather {

    private Double temp; // 온도

    private Double rainAmount; // 강수량

    private Double humid; // 습도

    private String lastUpdateTime; // 마지막 갱신 시각 (시간 단위)

    public WeatherResponseDTO.WeatherDto toDto() {
        return WeatherResponseDTO.WeatherDto.builder()
                .temp(temp)
                .rainAmount(rainAmount)
                .humid(humid)
                .lastUpdateTime(lastUpdateTime)
                .build();
    }
}