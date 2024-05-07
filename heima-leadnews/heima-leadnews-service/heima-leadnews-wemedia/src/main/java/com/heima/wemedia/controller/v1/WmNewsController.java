package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        return wmNewsService.findAll(dto);
    }

    @PostMapping("/submit")
    public ResponseResult findAll2(@RequestBody WmNewsDto dto) {
        return wmNewsService.submitNews(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto) {
        return wmNewsService.downOrUp(dto);
    }

    @PostMapping("/list_vo")
    public ResponseResult listAll(@RequestBody AuthDto dto){
        return wmNewsService.listAll(dto);
    }

    @GetMapping("/one_vo/{id}")
    public ResponseResult checkDetail(@PathVariable Integer id){
        return wmNewsService.checkDetail(id);
    }

    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody AuthDto authDto){
        return wmNewsService.authFail(authDto);
    }

    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody AuthDto authDto){
        return wmNewsService.authPass(authDto);
    }
}
