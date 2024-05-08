package com.heima.model.comment.dtos;

import lombok.Data;

@Data
public class CommentLikeDto {
    private String commentId;
    private Short operation;
}
