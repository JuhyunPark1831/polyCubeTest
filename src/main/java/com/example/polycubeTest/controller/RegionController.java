package com.example.polycubeTest.controller;

import com.example.polycubeTest.service.MidTermRegionService;
import com.example.polycubeTest.service.UltraShortTermRegionService;
import com.example.polycubeTest.service.ShortTermRegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/regions")
public class RegionController {

    @Autowired
    private UltraShortTermRegionService ultraShortTermRegionService;

    @Autowired
    private MidTermRegionService midTermRegionService;

    @Autowired
    private ShortTermRegionService shortTermRegionService;


    //지역 데이터 데이터베이스 삽입
    @GetMapping("/load-data")
    @ResponseBody
    public String loadRegionData() {
        shortTermRegionService.loadShortTermData(); //단기 예보
        midTermRegionService.loadMidTermData(); //중기 예보
        return ultraShortTermRegionService.loadUltraShortData(); //초단기 예보

    }
}