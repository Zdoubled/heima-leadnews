package com.heima.model.article.vos;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleCommentVO {
    private Long id;
    private String title;
    private Integer comments;
    private Boolean isComment;
    private Date publishTime;
}
