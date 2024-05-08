package com.heima.comment.service;

import com.heima.model.comment.dtos.CommentRepayDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCommentRepayService {
    /**
     * 新增回复
     * @param dto
     * @return
     */
    ResponseResult insert(CommentRepaySaveDto dto);

    /**
     * 查询评论回复列表
     * @param dto
     * @return
     */
    ResponseResult getByCommentId(CommentRepayDto dto);
}
