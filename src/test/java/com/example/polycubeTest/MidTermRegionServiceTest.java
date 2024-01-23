package com.example.polycubeTest;

import com.example.polycubeTest.entity.MidTermRegion;
import com.example.polycubeTest.repository.MidTermRegionRepository;
import com.example.polycubeTest.service.MidTermRegionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class MidTermRegionServiceTest {

    @InjectMocks
    private MidTermRegionService midTermRegionService;

    @Mock
    private MidTermRegionRepository midTermRegionRepository;

    @Test
    void testLoadMidTermData() throws IOException {
        // Resource 및 InputStream의 동작을 Mocking합니다.
        Resource resource = Mockito.mock(Resource.class);
        InputStream inputStream = Mockito.mock(InputStream.class);

        // readMidTermRegionsFromCsv 메서드의 동작을 Mocking합니다.
        List<MidTermRegion> expectedMidTermRegions = Arrays.asList(
                new MidTermRegion(1L, "Region1", "Code1"),
                new MidTermRegion(2L, "Region2", "Code2")
        );

        when(midTermRegionService.readMidTermRegionsFromCsv(inputStream)).thenReturn(expectedMidTermRegions);

        // saveAll 메서드의 동작을 Mocking합니다.
        when(midTermRegionRepository.saveAll(expectedMidTermRegions)).thenReturn(expectedMidTermRegions);

        // loadMidTermData 메서드를 호출합니다.
        String result = midTermRegionService.loadMidTermData();

        // 예상된 결과와 실제 결과를 비교합니다.
        assertEquals("Data loaded successfully.", result);

        // readMidTermRegionsFromCsv와 saveAll 메서드가 호출되었는지 검증합니다.
        Mockito.verify(midTermRegionService, Mockito.times(1)).readMidTermRegionsFromCsv(inputStream);
        Mockito.verify(midTermRegionRepository, Mockito.times(1)).saveAll(expectedMidTermRegions);
    }
}