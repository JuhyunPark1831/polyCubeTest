package com.example.polycubeTest.service;

import com.example.polycubeTest.entity.MidTermRegion;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class MidTermWeatherService {

    private final EntityManager em;

    @Value("${midweatherApi.serviceKey}")
    private String serviceKey;

    public ResponseEntity<Double> getMidTermRegionsWeather(Long regionId) {

        try {
            // 1. 날씨 정보를 요청한 지역 조회
            MidTermRegion midTermRegion = em.find(MidTermRegion.class, regionId);
            if (midTermRegion == null) {
                log.error("Region not found with id: {}", regionId);
                return ResponseEntity.notFound().build();
            }

            // 2. 요청 시각 조회
            LocalDateTime now = LocalDateTime.now();

            int currentHour = now.getHour();
            LocalDateTime closestDateTime;

            if (currentHour >= 18) {
                closestDateTime = now.withHour(18).withMinute(0).withSecond(0).withNano(0);
            } else if (currentHour >= 6) {
                closestDateTime = now.withHour(6).withMinute(0).withSecond(0).withNano(0);
            } else {
                closestDateTime = now.withHour(18).withMinute(0).withSecond(0).withNano(0).minusDays(1);
            }

            String tmFc = closestDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHH00"));

            // 기준 시각 조회 자료가 이미 존재하고 있다면 API 요청 없이 기존 자료 그대로 넘김
            double temperature = midTermRegion.getTemparature();
            if (temperature != -500.0) return ResponseEntity.ok(temperature);

            log.info("API 요청 발송 >>> 지역: {}, 연월일시각: {}", midTermRegion, tmFc);

            // 날씨 정보 초기화
            String regId = midTermRegion.getRegionCode();
            Double temp = initializeMidTermWeather(regId, tmFc);
            midTermRegion.updateRegionWeather(temp); // DB 업데이트

            return ResponseEntity.ok(temp);

        } catch (IOException e) {
            log.error("Error while fetching weather information", e);
            return ResponseEntity.ok(null);
        }
    }

    // 이 메서드는 API를 통해 날씨 정보를 초기화합니다.
    //초단기
    public Double initializeMidTermWeather(String regId, String tmFc)
            throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa");

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("regId", "UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmFc", "UTF-8") + "=" + URLEncoder.encode(tmFc, "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        log.info("request url: {}", url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String data = sb.toString();

        // 응답 수신 완료, 응답 결과를 JSON 파싱
        double taMin3 = 0;
        double taMax3 = 0;

        JSONObject jObject = new JSONObject(data);
        JSONObject response = jObject.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray jArray = items.getJSONArray("item");

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject obj = jArray.getJSONObject(i);
            taMin3 = obj.getDouble("taMin3");
            taMax3 = obj.getDouble("taMax3");

        }

        return (taMax3 + taMin3)/2;
    }
}
