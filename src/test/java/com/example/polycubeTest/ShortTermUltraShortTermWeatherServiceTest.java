package com.example.polycubeTest;

import com.example.polycubeTest.dto.WeatherResponseDTO;
import com.example.polycubeTest.entity.ShortTermRegion;
import com.example.polycubeTest.entity.Weather;
import com.example.polycubeTest.service.ShortTermWeatherService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShortTermUltraShortTermWeatherServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private ShortTermWeatherService shortTermWeatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetShortTermRegionsWeather_RegionNotFound() {
        // Arrange
        Long regionId = 1L;
        when(em.find(ShortTermRegion.class, regionId)).thenReturn(null);

        // Act
        ResponseEntity<WeatherResponseDTO> responseEntity = shortTermWeatherService.getShortTermRegionsWeather(regionId);

        // Assert
        assertEquals(ResponseEntity.notFound().build(), responseEntity);
    }

    @Test
    void testGetShortTermRegionsWeather_UseCachedData() throws IOException {
        // Arrange
        Long regionId = 1L;
        ShortTermRegion shortTermRegion = new ShortTermRegion();
        shortTermRegion.setNx(123);
        shortTermRegion.setNy(456);
        shortTermRegion.setId(regionId);

        when(em.find(ShortTermRegion.class, regionId)).thenReturn(shortTermRegion);

        LocalDateTime now = LocalDateTime.now();
        String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hourStr = now.format(DateTimeFormatter.ofPattern("HH00"));

        Weather prevWeather = new Weather(22.5, 5.0, 70.0, yyyyMMdd + " " + hourStr);
        shortTermRegion.setWeather(prevWeather);

        // Act
        ResponseEntity<WeatherResponseDTO> responseEntity = shortTermWeatherService.getShortTermRegionsWeather(regionId);

        // Assert
        assertNotNull(responseEntity.getBody());
        assertEquals("OK", responseEntity.getBody().getMessage());

        WeatherResponseDTO.WeatherDto weatherDTO = responseEntity.getBody().getWeather();
        assertNotNull(weatherDTO);
        assertEquals(22.5, weatherDTO.getTemp());
        assertEquals(5.0, weatherDTO.getRainAmount());
        assertEquals(70.0, weatherDTO.getHumid());

        // Verify that API request is not made
        verify(shortTermWeatherService, never()).initializeWeather(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testGetShortTermRegionsWeather_FetchDataFromAPI() throws IOException {
        // Arrange
        Long regionId = 1L;
        ShortTermRegion shortTermRegion = new ShortTermRegion();
        shortTermRegion.setNx(123);
        shortTermRegion.setNy(456);
        shortTermRegion.setId(regionId);

        when(em.find(ShortTermRegion.class, regionId)).thenReturn(shortTermRegion);

        LocalDateTime now = LocalDateTime.now();
        String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hourStr = now.format(DateTimeFormatter.ofPattern("HH00"));

        // Simulate that there is no cached data
        shortTermRegion.setWeather(null);

        // Mock initializeWeather method
        Weather mockWeather = new Weather(21.0, 0.0, 75.0, yyyyMMdd + " " + hourStr);
        doReturn(mockWeather).when(shortTermWeatherService).initializeWeather(anyString(), anyString(), anyString(), anyString(), anyString());

        // Act
        ResponseEntity<WeatherResponseDTO> responseEntity = shortTermWeatherService.getShortTermRegionsWeather(regionId);

        // Assert
        assertNotNull(responseEntity.getBody());
        assertEquals("OK", responseEntity.getBody().getMessage());

        WeatherResponseDTO.WeatherDto weatherDTO = responseEntity.getBody().getWeather();
        assertNotNull(weatherDTO);
        assertEquals(21.0, weatherDTO.getTemp());
        assertEquals(0.0, weatherDTO.getRainAmount());
        assertEquals(75.0, weatherDTO.getHumid());

        // Verify that API request is made
        verify(shortTermWeatherService).initializeWeather(yyyyMMdd, hourStr, "123", "456", yyyyMMdd + " " + hourStr);
        // Verify that Weather object is updated in the database
        assertNotNull(shortTermRegion.getWeather());
        assertEquals(21.0, shortTermRegion.getWeather().getTemp());
        assertEquals(0.0, shortTermRegion.getWeather().getRainAmount());
        assertEquals(75.0, shortTermRegion.getWeather().getHumid());
    }
}