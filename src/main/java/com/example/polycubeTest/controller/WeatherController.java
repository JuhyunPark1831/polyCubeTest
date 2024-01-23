package com.example.polycubeTest.controller;

import com.example.polycubeTest.dto.WeatherResponseDTO;
import com.example.polycubeTest.entity.Region;
import com.example.polycubeTest.entity.Weather;
//import com.example.polycubeTest.service.MidTermWeatherService;
import com.example.polycubeTest.service.MidTermWeatherService;
import com.example.polycubeTest.service.ShortTermRegionService;
import com.example.polycubeTest.service.ShortTermWeatherService;
import com.example.polycubeTest.service.WeatherService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ShortTermWeatherService shortTermWeatherService;

    @Autowired
    private MidTermWeatherService midTermWeatherService;

    @GetMapping("/ultrashort") //초단기 예보 api
    @Transactional
    public ResponseEntity<WeatherResponseDTO> getRegionWeather(@RequestParam Long regionId) {
        return weatherService.getRegionsWeather(regionId);
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