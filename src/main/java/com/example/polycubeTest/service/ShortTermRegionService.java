package com.example.polycubeTest.service;

import com.example.polycubeTest.entity.ShortTermRegion;
import com.example.polycubeTest.repository.ShortTermRegionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShortTermRegionService {

    @Autowired
    private ShortTermRegionRepository shortTermregionRepository;

    public String loadShortTermData() {
        try {
            // CSV 파일을 읽어서 Region 엔터티로 매핑하여 저장
            Resource resource = new ClassPathResource("init/regionList.csv");
            InputStream inputStream = resource.getInputStream();

            List<ShortTermRegion> shortTermRegions = readShortTermRegionsFromCsv(inputStream);
            shortTermregionRepository.saveAll(shortTermRegions);

            return "Data loaded successfully.";
        } catch (IOException e) {
            log.error("Failed to load data.", e);
            return "Failed to load data.";
        }
    }

    public List<ShortTermRegion> readShortTermRegionsFromCsv(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines()
                    .skip(1) // Skip the header line
                    .map(this::mapToShortTermRegion)
                    .collect(Collectors.toList());
        }
    }
    public ShortTermRegion mapToShortTermRegion(String line) {
        String[] parts = line.split(",");

        Long id = Long.parseLong(parts[0]);
        String parentRegion = parts[1];
        String childRegion = parts[2];
        int nx = Integer.parseInt(parts[3]);
        int ny = Integer.parseInt(parts[4]);

        return new ShortTermRegion(id, parentRegion, childRegion, nx, ny);
    }
}