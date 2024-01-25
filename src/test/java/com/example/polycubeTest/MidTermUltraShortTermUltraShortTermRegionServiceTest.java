package com.example.polycubeTest;

import com.example.polycubeTest.entity.MidTermRegion;
import com.example.polycubeTest.repository.MidTermRegionRepository;
import com.example.polycubeTest.service.MidTermRegionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MidTermUltraShortTermUltraShortTermRegionServiceTest {

    @Mock
    private MidTermRegionRepository midTermRegionRepository;

    @InjectMocks
    private MidTermRegionService midTermRegionService;

    @Test
    void testLoadMidTermData_Success() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock 데이터 설정
        Resource resource = new ClassPathResource("init/test-midTermRegionList.csv");
        InputStream inputStream = resource.getInputStream();
        List<MidTermRegion> mockData = List.of(
                new MidTermRegion(1L, "백령도", "11A00101"),
                new MidTermRegion(2L, "서울", "11B10101")
        );

        // readMidTermRegionsFromCsv 호출에 대한 Mock 설정
        when(midTermRegionService.readMidTermRegionsFromCsv(inputStream)).thenReturn(mockData);

        // 테스트 실행
        String result = midTermRegionService.loadMidTermData();

        // 결과 확인
        assertEquals("Data loaded successfully.", result);

        // Repository의 saveAll 메서드가 호출되었는지 확인
        verify(midTermRegionRepository, times(1)).saveAll(mockData);
    }

    @Test
    void testReadMidTermRegionsFromCsv() throws IOException {
        MockitoAnnotations.openMocks(this);

        // 테스트 데이터 설정
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("init/test-midTermRegionList.csv");

        // 테스트 실행
        List<MidTermRegion> result = midTermRegionService.readMidTermRegionsFromCsv(inputStream);

        // 결과 확인
        assertEquals(2, result.size());
        assertEquals("백령도", result.get(0).getRegion());
        assertEquals("11A00101", result.get(0).getRegionCode());
        assertEquals("서울", result.get(1).getRegion());
        assertEquals("11B10101", result.get(1).getRegionCode());
    }
}