package com.heima.model.article.vos;

import lombok.Data;

@Data
public class ArticleStatisticsVO {
    private Long id;
    private String title;
    private Integer likes;
    private Integer collection;
    private Integer comment;
    private Integer views;
}
