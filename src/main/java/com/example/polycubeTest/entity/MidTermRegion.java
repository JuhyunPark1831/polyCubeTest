package com.example.polycubeTest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter //for Testing
@Getter
@Entity
@NoArgsConstructor
public class MidTermRegion {

    @Id
    @Column(name = "region_id")
    private Long id; // 지역 순번

    @Column(name = "region")
    private String region; // 지역 이름

    @Column(name = "region_code")
    private String regionCode; // 지역 코드

    private Double temparature = -500.0; // 3일간 최대 최소 온도 평균

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