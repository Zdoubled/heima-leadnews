package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.Article.IArticleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.StatisticsDto;
import com.heima.utils.common.DateUtils;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.service.WmStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j
public class WmStatisticsServiceImpl implements WmStatisticsService {
    @Autowired
    private IArticleClient articleClient;

    @Override
    public ResponseResult newsDimension(String beginDate, String endDate) {
        log.info("查询统计数据开始：{}，结束：{}",beginDate,endDate);
        HashMap<String, Object> result = new HashMap<>();
        Integer wmUserId = WmThreadLocalUtil.getUser().getId();
        Date begD = DateUtils.stringToDate(beginDate);
        Date endD = DateUtils.stringToDate(endDate);
        log.info("类型转化：{}，结束：{}",begD,endD);

        ResponseResult responseResult = articleClient.queryLikesAndCollections(wmUserId,begD, endD);
        if (responseResult.getCode().equals(200)) {
            String jsonString = JSON.toJSONString(responseResult.getData());
            HashMap map = JSON.parseObject(jsonString, HashMap.class);
            result.put("likesNum", map.get("likes") == null ? 0 : map.get("likes"));
            result.put("collectNum", map.get("collections") == null ? 0 : map.get("collections"));
            result.put("publishNum", map.get("newsCount") == null ? 0 : map.get("newsCount"));
        }
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult newsPage(StatisticsDto dto) {
        log.info("查询统计数据开始：{}，结束：{}",dto.getBeginDate(), dto.getEndDate());
        dto.checkParam();
        Integer wmUserId = WmThreadLocalUtil.getUser().getId();
        dto.setWmUserId(wmUserId);
        return articleClient.findNewPage(dto);
    }
}
