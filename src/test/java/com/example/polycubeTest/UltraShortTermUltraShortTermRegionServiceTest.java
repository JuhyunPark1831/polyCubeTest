package com.example.polycubeTest;
import com.example.polycubeTest.entity.UltraShortTermRegion;
import com.example.polycubeTest.repository.UltraShortTermRegionRepository;
import com.example.polycubeTest.service.UltraShortTermRegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UltraShortTermUltraShortTermRegionServiceTest {

    @Mock
    private UltraShortTermRegionRepository ultraShortTermRegionRepository;

    @InjectMocks
    private UltraShortTermRegionService ultraShortTermRegionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadData_Success() throws IOException {
        // Arrange
        InputStream inputStream = mock(InputStream.class);
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(inputStream);

        // Mock readRegionsFromCsv method
        List<UltraShortTermRegion> ultraShortTermRegions = Arrays.asList(
                new UltraShortTermRegion(1L, "Parent1", "Child1", 1, 2),
                new UltraShortTermRegion(2L, "Parent2", "Child2", 3, 4)
        );
        doReturn(ultraShortTermRegions).when(ultraShortTermRegionService).readRegionsFromCsv(inputStream);

        // Act
        String result = ultraShortTermRegionService.loadData();

        // Assert
        assertEquals("Data loaded successfully.", result);

        // Verify that saveAll method is called with the correct regions
        verify(ultraShortTermRegionRepository).saveAll(ultraShortTermRegions);
    }

    @Test
    void testLoadData_Failure() throws IOException {
        // Arrange
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenThrow(IOException.class);

        // Act
        String result = ultraShortTermRegionService.loadData();

        // Assert
        assertEquals("Failed to load data.", result);

        // Verify that saveAll method is not called
        verify(ultraShortTermRegionRepository, never()).saveAll(any());
    }

    @Test
    void testReadRegionsFromCsv() throws IOException {
        // Arrange
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test-regionList.csv");

        // Act
        List<UltraShortTermRegion> ultraShortTermRegions = ultraShortTermRegionService.readRegionsFromCsv(inputStream);

        // Assert
        assertEquals(2, ultraShortTermRegions.size());
        assertEquals("Parent1", ultraShortTermRegions.get(0).getParentRegion());
        assertEquals("Child1", ultraShortTermRegions.get(0).getChildRegion());
        assertEquals(1, ultraShortTermRegions.get(0).getNx());
        assertEquals(2, ultraShortTermRegions.get(0).getNy());
        assertEquals("Parent2", ultraShortTermRegions.get(1).getParentRegion());
        assertEquals("Child2", ultraShortTermRegions.get(1).getChildRegion());
        assertEquals(3, ultraShortTermRegions.get(1).getNx());
        assertEquals(4, ultraShortTermRegions.get(1).getNy());
    }

    @Test
    void testMapToRegion() {
        // Arrange
        String line = "1,Parent1,Child1,1,2";

        // Act
        UltraShortTermRegion ultraShortTermRegion = ultraShortTermRegionService.mapToRegion(line);

        // Assert
        assertEquals(1L, ultraShortTermRegion.getId());
        assertEquals("Parent1", ultraShortTermRegion.getParentRegion());
        assertEquals("Child1", ultraShortTermRegion.getChildRegion());
        assertEquals(1, ultraShortTermRegion.getNx());
        assertEquals(2, ultraShortTermRegion.getNy());
    }
}