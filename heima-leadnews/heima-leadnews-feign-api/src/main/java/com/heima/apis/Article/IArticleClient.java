package com.heima.apis.Article;

import com.heima.apis.fallback.IArticleClientFallback;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.ArticleCommentStatusDto;
import com.heima.model.wemedia.dtos.StatisticsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@FeignClient(value = "leadnews-article", fallback = IArticleClientFallback.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto);

    @GetMapping("/api/v1/article/{articleId}")
    public ResponseResult getApArticleConfigByArticleId(@PathVariable Long articleId);

    @PostMapping("/api/v1/article/update_article_comment_status")
    public ResponseResult updateArticleCommentStatus(@RequestBody ArticleCommentStatusDto dto);

    @PostMapping("/api/v1/article/find_news_comments")
    public ResponseResult findNewsComments(@RequestBody ArticleCommentDto dto);

    @GetMapping("/api/v1/article/query_likes_and_collections")
    public ResponseResult queryLikesAndCollections(@RequestParam("wmUserId") Integer wmUserId,@RequestParam("beginDate") Date beginDate,@RequestParam("endDate") Date endDate);

    @PostMapping("/api/v1/article/find_new_page")
    public ResponseResult findNewPage(@RequestBody StatisticsDto dto);
}
