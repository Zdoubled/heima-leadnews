package com.heima.behavior.controller;

import com.heima.behavior.service.UnlikesBehaviorService;
import com.heima.model.behavior.dtos.UnlikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/un_likes_behavior")
public class UnlikesBehaviorController {

    @Autowired
    private UnlikesBehaviorService unlikesBehaviorService;

    @PostMapping
    public ResponseResult unlikesBehavior(@RequestBody UnlikesBehaviorDto dto) {
        return unlikesBehaviorService.unlikes(dto);
    }
}
