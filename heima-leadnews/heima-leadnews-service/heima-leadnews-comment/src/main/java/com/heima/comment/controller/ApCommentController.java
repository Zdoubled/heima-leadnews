package com.heima.comment.controller;

import com.heima.comment.service.ApCommentService;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/comment")
public class ApCommentController {

    @Autowired
    private ApCommentService apCommentService;

    @PostMapping("/save")
    public ResponseResult insert(@RequestBody CommentSaveDto dto){
        return apCommentService.insert(dto);
    }

    @PostMapping("/load")
    public ResponseResult getAll(@RequestBody CommentDto dto){
        return apCommentService.getByArticleId(dto);
    }
    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentLikeDto dto){
        return apCommentService.like(dto);
    }
}
