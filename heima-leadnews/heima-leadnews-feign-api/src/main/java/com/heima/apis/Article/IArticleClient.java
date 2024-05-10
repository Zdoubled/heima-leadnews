package com.heima.apis.Article;

import com.heima.apis.fallback.IArticleClientFallback;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.ArticleCommentStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
