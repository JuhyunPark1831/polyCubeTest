package com.example.polycubeTest;

import com.example.polycubeTest.dto.WeatherResponseDTO;
import com.example.polycubeTest.entity.UltraShortTermRegion;
import com.example.polycubeTest.service.UltraShortTermWeatherService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UltraShortTermWeatherServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private UltraShortTermWeatherService ultraShortTermWeatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRegionsWeather_RegionNotFound() {
        // Arrange
        Long regionId = 1L;
        when(em.find(UltraShortTermRegion.class, regionId)).thenReturn(null);

        // Act
        ResponseEntity<WeatherResponseDTO> responseEntity = ultraShortTermWeatherService.getRegionsWeather(regionId);

        // Assert
        assertEquals(ResponseEntity.notFound().build(), responseEntity);
    }

    private InputStream getResourceAsStream(String resourceName) {
        // Load resource from the classpath
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }
}