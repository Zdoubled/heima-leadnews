package com.heima.comment.service;

import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCommentService {
    /**
     * 新增评论
     * @param dto
     * @return
     */
    ResponseResult insert(CommentSaveDto dto);

    /**
     * 查询所有评论
     * @param dto
     * @return
     */
    ResponseResult getByArticleId(CommentDto dto);

    /**
     * 点赞评论
     * @param dto
     * @return
     */
    ResponseResult like(CommentLikeDto dto);
}
