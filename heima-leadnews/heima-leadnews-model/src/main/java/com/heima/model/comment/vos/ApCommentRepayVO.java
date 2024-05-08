package com.heima.model.comment.vos;

import lombok.Data;

import java.util.Date;

@Data
public class ApCommentRepayVO {
    private String id;
    private Integer authorId;
    private String authorName;
    private String content;
    private Date createTime;
    private Integer likes;
    private Short operation;
}
