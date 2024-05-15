package com.heima.wemedia.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.StatisticsDto;

import java.util.Date;

public interface WmStatisticsService {
    /**
     * 图文统计
     * @param beginDate
     * @param endDate
     * @return
     */
    ResponseResult newsDimension(String beginDate, String endDate);

    /**
     * 分页展示文章列表，展示当前时间范围内的具体文章阅读、评论、收藏的数量。
     * @param dto
     * @return
     */
    ResponseResult newsPage(StatisticsDto dto);
}
