package com.heima.model.wemedia.dtos;

import lombok.Data;

@Data
public class ArticleCommentStatusDto {
    private Long articleId;
    /**
     * 0 关闭评论； 1 开启评论
     */
    private Short operation;
}
