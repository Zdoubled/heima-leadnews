package com.heima.wemedia.service;

import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ArticleCommentDetailDto;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.ArticleCommentStatusDto;

public interface CommentManageService {
    /**
     * 删除评论
     * @param commentId
     * @return
     */
    ResponseResult removeCommentByCommentId(String commentId);

    /***
     * 删除评论回复
     * @param commentRepayId
     * @return
     */
    ResponseResult removeCommentRepayByCommentRepayId(String commentRepayId);

    /**
     * 新增作者回复评论
     * @param dto
     * @return
     */
    ResponseResult saveCommentRepay(CommentRepaySaveDto dto);

    /**
     * 文章评论功能
     * 0 关闭； 1 开启
     * @param dto
     * @return
     */
    ResponseResult updateCommentStatus(ArticleCommentStatusDto dto);

    /**
     * 文章评论列表总览
     * @param dto
     * @return
     */
    ResponseResult findNewsComments(ArticleCommentDto dto);

    /**
     * 文章评论列表
     * @param dto
     * @return
     */
    ResponseResult list(ArticleCommentDetailDto dto);

    /**
     * 点赞操作
     * @param dto
     * @return
     */
    ResponseResult like(CommentLikeDto dto);
}
