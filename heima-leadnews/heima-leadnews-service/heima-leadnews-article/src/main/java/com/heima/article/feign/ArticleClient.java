package com.heima.article.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.Article.IArticleClient;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.ArticleCommentStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService apArticleService;
    @Autowired
    private ApArticleConfigService apArticleConfigService;
    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }

    @Override
    @GetMapping("/api/v1/article/{articleId}")
    public ResponseResult getApArticleConfigByArticleId(@PathVariable Long articleId) {
        return ResponseResult.okResult(apArticleConfigService.getOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, articleId)));
    }

    @Override
    @PostMapping("/api/v1/article/update_article_comment_status")
    public ResponseResult updateArticleCommentStatus(@RequestBody ArticleCommentStatusDto dto) {
        apArticleConfigService.update(Wrappers.<ApArticleConfig>lambdaUpdate().set(ApArticleConfig::getIsComment, dto.getOperation()).eq(ApArticleConfig::getArticleId, dto.getArticleId()));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    @PostMapping("/api/v1/article/find_news_comments")
    public ResponseResult findNewsComments(@RequestBody ArticleCommentDto dto) {
        return apArticleService.findNewsComments(dto);
    }


}
