package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.StatisticsDto;

import java.util.Date;

public interface ApArticleService extends IService<ApArticle> {

    /**
     * 根据参数加载文章列表
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    ResponseResult load(Short loadtype, ArticleHomeDto dto);

    /**
     * 根据参数加载文章列表
     * @param loadtype 是否为首页
     * @param dto
     * @return
     */
    ResponseResult load2(Short loadtype, ArticleHomeDto dto, Boolean firstPage);

    /**
     * 保存app端相关文章
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDto dto);

    /**
     * 回显用户对文章行为信息
     * @param dto
     * @return
     */
    ResponseResult loadArticleBehavior(ArticleInfoDto dto);

    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     * @param mess
     */
    public void updateScore(ArticleVisitStreamMess mess);

    /**
     * 查看评论列表
     * @param dto
     * @return
     */
    ResponseResult findNewsComments(ArticleCommentDto dto);

    /**
     * 点赞收藏统计
     * @param wmUserId
     * @param beginDate
     * @param endDate
     * @return
     */
    ResponseResult queryLikesAndCollections(Integer wmUserId,Date beginDate, Date endDate);

    /**
     * 分页展示文章列表，展示当前时间范围内的具体文章阅读、评论、收藏的数量。
     * @param dto
     * @return
     */
    ResponseResult findNewPage(StatisticsDto dto);
}