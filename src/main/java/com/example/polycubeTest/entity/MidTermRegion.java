package com.example.polycubeTest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class MidTermRegion {

    @Id
    @Column(name = "region_id")
    private Long id; // 지역 순번

    @Column(name = "region")
    private String region; // 시, 도

    @Column(name = "region_code")
    private String regionCode; // 시, 군, 구

    private Double temparature = -500.0; // 지역 날씨 정보

    // 날씨 정보 제외하고 지역 생성
    public MidTermRegion(Long id, String region, String regionCode) {
        this.id = id;
        this.region = region;
        this.regionCode = regionCode;
    }

    // 날씨 갱신
    public void updateRegionWeather(Double temparature) {
        this.temparature = temparature;
    }
}