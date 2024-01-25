package com.example.polycubeTest.service;

import com.example.polycubeTest.entity.MidTermRegion;
import com.example.polycubeTest.repository.MidTermRegionRepository;
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
public class MidTermRegionService {

    @Autowired
    private MidTermRegionRepository midTermRegionRepository;

    public String loadMidTermData() {
        try {
            // CSV 파일을 읽어서 Region 엔터티로 매핑하여 저장
            Resource resource = new ClassPathResource("init/midTermRegionList.csv");
            InputStream inputStream = resource.getInputStream();

            List<MidTermRegion> midTermregions = readMidTermRegionsFromCsv(inputStream);
            midTermRegionRepository.saveAll(midTermregions);

            return "Data loaded successfully.";
        } catch (IOException e) {
            log.error("Failed to load data.", e);
            return "Failed to load data.";
        }
    }

    public List<MidTermRegion> readMidTermRegionsFromCsv(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            br.mark(1);
            int bom = br.read();
            if (bom != 0xFEFF) {
                br.reset();
            }
            return br.lines()
                    .map(this::mapToMidTermRegion)
                    .collect(Collectors.toList());
        }
    }
    private MidTermRegion mapToMidTermRegion(String line) {
        String[] parts = line.split(",");

        Long id = Long.parseLong(parts[0]);
        String region = parts[1];
        String regionCode = parts[2];

        return new MidTermRegion(id, region, regionCode);
    }
}