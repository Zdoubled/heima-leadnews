package com.heima.apis.fallback;

import com.heima.apis.Article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.ArticleCommentStatusDto;
import org.springframework.stereotype.Component;

/**
 * feign失败配置
 */
@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"服务调用失败");
    }

    @Override
    public ResponseResult getApArticleConfigByArticleId(Long articleId) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"服务调用失败");
    }

    @Override
    public ResponseResult updateArticleCommentStatus(ArticleCommentStatusDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"服务调用失败");
    }

    @Override
    public ResponseResult findNewsComments(ArticleCommentDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"服务调用失败");
    }
}
