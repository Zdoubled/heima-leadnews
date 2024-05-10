package com.heima.wemedia.controller.v1;

import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ArticleCommentDetailDto;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.ArticleCommentStatusDto;
import com.heima.wemedia.service.CommentManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment/manage")
public class CommentManageController {
    @Autowired
    private CommentManageService commentManageService;

    @DeleteMapping("/del_comment/{commentId}")
    public ResponseResult removeComment(@PathVariable String commentId){
        return commentManageService.removeCommentByCommentId(commentId);
    }

    @DeleteMapping("/del_comment_repay/{commentRepayId}")
    public ResponseResult removeCommentRepay(@PathVariable String commentRepayId){
        return commentManageService.removeCommentRepayByCommentRepayId(commentRepayId);
    }

    @PostMapping("/comment_repay")
    public ResponseResult repayComment(@RequestBody CommentRepaySaveDto dto) {
        return commentManageService.saveCommentRepay(dto);
    }

    @PostMapping("/update_comment_status")
    public ResponseResult updateCommentStatus(@RequestBody ArticleCommentStatusDto dto) {
        return commentManageService.updateCommentStatus(dto);
    }

    @PostMapping("/find_news_comments")
    public ResponseResult findNewsComments(ArticleCommentDto dto){
        return commentManageService.findNewsComments(dto);
    }

    @PostMapping("/list")
    public ResponseResult list(@RequestBody ArticleCommentDetailDto dto){
        return commentManageService.list(dto);
    }

    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentLikeDto dto){
        return commentManageService.like(dto);
    }
}
