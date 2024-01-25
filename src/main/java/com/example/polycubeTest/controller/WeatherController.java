package com.example.polycubeTest.controller;

import com.example.polycubeTest.dto.WeatherResponseDTO;
import com.example.polycubeTest.service.MidTermWeatherService;
import com.example.polycubeTest.service.ShortTermWeatherService;
import com.example.polycubeTest.service.UltraShortTermWeatherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    @Autowired
    private UltraShortTermWeatherService ultraShortTermWeatherService;

    @Autowired
    private ShortTermWeatherService shortTermWeatherService;

    @Autowired
    private MidTermWeatherService midTermWeatherService;

    @GetMapping("/ultrashort") //초단기 예보 api
    @Transactional
    public ResponseEntity<WeatherResponseDTO> getRegionWeather(@RequestParam Long regionId) {
        return ultraShortTermWeatherService.getUltraShortTermRegionsWeather(regionId);
    }

    @GetMapping("/short") //단기 예보 api
    @Transactional
    public ResponseEntity<WeatherResponseDTO> getShortTermRegionWeather(@RequestParam Long regionId) {
        return shortTermWeatherService.getShortTermRegionsWeather(regionId);
    }

    @GetMapping("/mid") //중기 예보 api
    @Transactional
    public ResponseEntity<Double> getMidTermRegionWeather(@RequestParam Long regionId) {
        return midTermWeatherService.getMidTermRegionsWeather(regionId);
    }
}