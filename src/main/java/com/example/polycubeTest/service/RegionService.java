package com.example.polycubeTest.service;

import com.example.polycubeTest.entity.Region;
import com.example.polycubeTest.repository.RegionRepository;
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
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    public String loadData() {
        try {
            // CSV 파일을 읽어서 Region 엔터티로 매핑하여 저장
            Resource resource = new ClassPathResource("init/regionList.csv");
            InputStream inputStream = resource.getInputStream();

            List<Region> regions = readRegionsFromCsv(inputStream);
            regionRepository.saveAll(regions);

            return "Data loaded successfully.";
        } catch (IOException e) {
            log.error("Failed to load data.", e);
            return "Failed to load data.";
        }
    }

    private List<Region> readRegionsFromCsv(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines()
                    .skip(1) // Skip the header line
                    .map(this::mapToRegion)
                    .collect(Collectors.toList());
        }
    }
    private Region mapToRegion(String line) {
        String[] parts = line.split(",");

        Long id = Long.parseLong(parts[0]);
        String parentRegion = parts[1];
        String childRegion = parts[2];
        int nx = Integer.parseInt(parts[3]);
        int ny = Integer.parseInt(parts[4]);

        return new Region(id, parentRegion, childRegion, nx, ny);
    }
}