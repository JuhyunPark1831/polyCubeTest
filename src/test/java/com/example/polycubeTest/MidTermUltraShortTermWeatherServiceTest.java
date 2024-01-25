package com.example.polycubeTest;

import com.example.polycubeTest.entity.MidTermRegion;
import com.example.polycubeTest.service.MidTermWeatherService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MidTermUltraShortTermWeatherServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private MidTermWeatherService midTermWeatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMidTermRegionsWeather_RegionNotFound() {
        // Arrange
        Long regionId = 1L;
        when(em.find(MidTermRegion.class, regionId)).thenReturn(null);

        // Act
        ResponseEntity<Double> responseEntity = midTermWeatherService.getMidTermRegionsWeather(regionId);

        // Assert
        assertEquals(ResponseEntity.notFound().build(), responseEntity);
    }

    @Test
    void testGetMidTermRegionsWeather_TemperatureExists() throws IOException {
        // Arrange
        Long regionId = 1L;
        MidTermRegion midTermRegion = new MidTermRegion();
        midTermRegion.setTemparature(25.0); // Set a temperature value
        when(em.find(MidTermRegion.class, regionId)).thenReturn(midTermRegion);

        // Act
        ResponseEntity<Double> responseEntity = midTermWeatherService.getMidTermRegionsWeather(regionId);

        // Assert
        assertEquals(ResponseEntity.ok(25.0), responseEntity);
        // Verify that initializeMidTermWeather method is not called
        verify(midTermWeatherService, never()).initializeMidTermWeather(any(), any());
    }

    @Test
    void testGetMidTermRegionsWeather_TemperatureDoesNotExist() throws IOException {
        // Arrange
        Long regionId = 1L;
        MidTermRegion midTermRegion = new MidTermRegion();
        midTermRegion.setRegionCode("11A00101");
        when(em.find(MidTermRegion.class, regionId)).thenReturn(midTermRegion);

        // Mock initializeMidTermWeather to return a specific value
        doReturn(20.0).when(midTermWeatherService).initializeMidTermWeather("11A00101", "202401230600");

        // Act
        ResponseEntity<Double> responseEntity = midTermWeatherService.getMidTermRegionsWeather(regionId);

        // Assert
        assertEquals(ResponseEntity.ok(20.0), responseEntity);
        // Verify that initializeMidTermWeather method is called with correct parameters
        verify(midTermWeatherService).initializeMidTermWeather("11A00101", "202401230600");
        // Verify that MidTermRegion is updated with the new temperature value
        verify(midTermRegion).updateRegionWeather(20.0);
    }

    @Test
    void testInitializeMidTermWeather() throws IOException {
        // Arrange
        MidTermWeatherService spyService = spy(midTermWeatherService);

        // Mocking EntityManager.find to return a valid region
        MidTermRegion midTermRegion = new MidTermRegion();
        midTermRegion.setRegionCode("11A00101");
        when(em.find(MidTermRegion.class, anyLong())).thenReturn(midTermRegion);

        // Act
        ResponseEntity<Double> responseEntity = spyService.getMidTermRegionsWeather(1L);

        // Assert
        assertEquals(ResponseEntity.ok(20.0), responseEntity);

        // Verify that initializeMidTermWeather method is called with correct parameters
        verify(spyService).initializeMidTermWeather("11A00101", "202401230600");
        // Verify that MidTermRegion is updated with the new temperature value
        verify(midTermRegion).updateRegionWeather(20.0);
    }
}