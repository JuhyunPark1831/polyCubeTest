package com.example.polycubeTest.service;


import com.example.polycubeTest.dto.WeatherResponseDTO;
import com.example.polycubeTest.entity.ShortTermRegion;
import com.example.polycubeTest.entity.Weather;
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
public class ShortTermWeatherService {

    private final EntityManager em;

    @Value("${weatherApi.serviceKey}")
    private String serviceKey;

    public ResponseEntity<WeatherResponseDTO> getShortTermRegionsWeather(Long regionId) {

        try {
            // 1. 날씨 정보를 요청한 지역 조회
            ShortTermRegion shortTermRegion = em.find(ShortTermRegion.class, regionId);
            if (shortTermRegion == null) {
                log.error("Region not found with id: {}", regionId);
                return ResponseEntity.notFound().build();
            }

            // 2. 요청 시각 조회
            LocalDateTime now = LocalDateTime.now();
            String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            yyyyMMdd = "20240123";

            // 날짜와 시간을 형식에 맞게 조정
            int currentHour = now.getHour();
            int[] forecastHours = {2, 5, 8, 11, 14, 17, 20, 23};
            int closestHour = forecastHours[0];
            for (int hour : forecastHours) {
                if (currentHour >= hour) {
                    closestHour = hour;
                } else {
                    break;
                }
            }

            String hourStr = String.format("%02d00", closestHour); // 가장 최근 시간 기준
            String nx = Integer.toString(shortTermRegion.getNx());
            String ny = Integer.toString(shortTermRegion.getNy());
            String currentChangeTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd ")) + hourStr;

            // 기준 시각 조회 자료가 이미 존재하고 있다면 API 요청 없이 기존 자료 그대로 넘김
            Weather prevWeather = shortTermRegion.getWeather();
            if (prevWeather != null && prevWeather.getLastUpdateTime() != null) {
                if (prevWeather.getLastUpdateTime().equals(currentChangeTime)) {
                    log.info("기존 자료를 재사용합니다");
                    WeatherResponseDTO dto = WeatherResponseDTO.builder()
                            .weather(prevWeather.toDto())
                            .message("OK")
                            .build();
                    return ResponseEntity.ok(dto);
                }
            }

            log.info("API 요청 발송 >>> 지역: {}, 연월일: {}, 시각: {}", shortTermRegion, yyyyMMdd, hourStr);

            // 날씨 정보 초기화
            Weather weather = initializeWeather(yyyyMMdd, hourStr, nx, ny, currentChangeTime);
            shortTermRegion.updateRegionWeather(weather); // DB 업데이트

            WeatherResponseDTO dto = WeatherResponseDTO.builder()
                    .weather(weather.toDto())
                    .message("OK")
                    .build();
            return ResponseEntity.ok(dto);

        } catch (IOException e) {
            log.error("Error while fetching weather information", e);
            WeatherResponseDTO dto = WeatherResponseDTO.builder()
                    .weather(null)
                    .message("날씨 정보를 불러오는 중 오류가 발생했습니다")
                    .build();
            return ResponseEntity.ok(dto);
        }
    }

    // 이 메서드는 API를 통해 날씨 정보를 초기화합니다.
    // 초단기
    private Weather initializeWeather(String yyyyMMdd, String hourStr, String nx, String ny, String currentChangeTime)
            throws IOException, UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(yyyyMMdd, "UTF-8")); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(hourStr, "UTF-8")); /*06시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/

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

        Double temp = null;
        Double humid = null;
        Double rainAmount = null;

        JSONObject jObject = new JSONObject(data);
        JSONObject response = jObject.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray jArray = items.getJSONArray("item");

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject obj = jArray.getJSONObject(i);
            String category = obj.getString("category");
            String fcstValue = obj.getString("fcstValue");

            switch (category) {
                case "TMP":
                    temp = Double.valueOf(fcstValue);
                    break;
                case "PCP":
                    if (fcstValue.equals("강수없음")) fcstValue = "0";
                    rainAmount = Double.valueOf(fcstValue);
                    break;
                case "REH":
                    humid = Double.valueOf(fcstValue);
                    break;
            }
        }

        return new Weather(temp, rainAmount, humid, currentChangeTime);
    }


}