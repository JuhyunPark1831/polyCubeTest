package com.example.polycubeTest;

import com.example.polycubeTest.entity.ShortTermRegion;
import com.example.polycubeTest.repository.ShortTermRegionRepository;
import com.example.polycubeTest.service.ShortTermRegionService;
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

class ShortTermUltraShortTermUltraShortTermRegionServiceTest {

    @Mock
    private ShortTermRegionRepository shortTermRegionRepository;

    @InjectMocks
    private ShortTermRegionService shortTermRegionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadShortTermData_Success() throws IOException {
        // Arrange
        InputStream inputStream = mock(InputStream.class);
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(inputStream);

        // Mock readShortTermRegionsFromCsv method
        List<ShortTermRegion> shortTermRegions = Arrays.asList(
                new ShortTermRegion(1L, "Parent1", "Child1", 1, 2),
                new ShortTermRegion(2L, "Parent2", "Child2", 3, 4)
        );
        doReturn(shortTermRegions).when(shortTermRegionService).readShortTermRegionsFromCsv(inputStream);

        // Act
        String result = shortTermRegionService.loadShortTermData();

        // Assert
        assertEquals("Data loaded successfully.", result);

        // Verify that saveAll method is called with the correct shortTermRegions
        verify(shortTermRegionRepository).saveAll(shortTermRegions);
    }

    @Test
    void testLoadShortTermData_Failure() throws IOException {
        // Arrange
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenThrow(IOException.class);

        // Act
        String result = shortTermRegionService.loadShortTermData();

        // Assert
        assertEquals("Failed to load data.", result);

        // Verify that saveAll method is not called
        verify(shortTermRegionRepository, never()).saveAll(any());
    }

    @Test
    void testReadShortTermRegionsFromCsv() throws IOException {
        // Arrange
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test-regionList.csv");

        // Act
        List<ShortTermRegion> shortTermRegions = shortTermRegionService.readShortTermRegionsFromCsv(inputStream);

        // Assert
        assertEquals(2, shortTermRegions.size());
        assertEquals("Parent1", shortTermRegions.get(0).getParentRegion());
        assertEquals("Child1", shortTermRegions.get(0).getChildRegion());
        assertEquals(1, shortTermRegions.get(0).getNx());
        assertEquals(2, shortTermRegions.get(0).getNy());
        assertEquals("Parent2", shortTermRegions.get(1).getParentRegion());
        assertEquals("Child2", shortTermRegions.get(1).getChildRegion());
        assertEquals(3, shortTermRegions.get(1).getNx());
        assertEquals(4, shortTermRegions.get(1).getNy());
    }

    @Test
    void testMapToShortTermRegion() {
        // Arrange
        String line = "1,Parent1,Child1,1,2";

        // Act
        ShortTermRegion shortTermRegion = shortTermRegionService.mapToShortTermRegion(line);

        // Assert
        assertEquals(1L, shortTermRegion.getId());
        assertEquals("Parent1", shortTermRegion.getParentRegion());
        assertEquals("Child1", shortTermRegion.getChildRegion());
        assertEquals(1, shortTermRegion.getNx());
        assertEquals(2, shortTermRegion.getNy());
    }
}