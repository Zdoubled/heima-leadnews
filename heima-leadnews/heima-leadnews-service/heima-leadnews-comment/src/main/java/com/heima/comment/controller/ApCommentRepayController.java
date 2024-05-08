package com.heima.comment.controller;

import com.heima.comment.service.ApCommentRepayService;
import com.heima.model.comment.dtos.CommentRepayDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment_repay")
public class ApCommentRepayController {
    @Autowired
    private ApCommentRepayService apCommentRepayService;

    @PostMapping("/save")
    public ResponseResult save(@RequestBody CommentRepaySaveDto dto){
        return apCommentRepayService.insert(dto);
    }

    @PostMapping("/load")
    public ResponseResult load(@RequestBody CommentRepayDto dto){
        return apCommentRepayService.getByCommentId(dto);
    }
}
