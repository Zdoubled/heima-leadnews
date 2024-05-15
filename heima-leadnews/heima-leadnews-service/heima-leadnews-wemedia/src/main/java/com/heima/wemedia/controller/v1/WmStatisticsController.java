package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.StatisticsDto;
import com.heima.wemedia.service.WmStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/statistics")
public class WmStatisticsController {
    @Autowired
    private WmStatisticsService wmStatisticsService;

    @GetMapping("/newsDimension")
    public ResponseResult newsDimension(@RequestParam("beginDate") String beginDate,@RequestParam("endDate") String endDate) {
        return wmStatisticsService.newsDimension(beginDate, endDate);
    }

    @GetMapping("/newsPage")
    public ResponseResult newsPage(StatisticsDto dto) {
        return wmStatisticsService.newsPage(dto);
    }
}
