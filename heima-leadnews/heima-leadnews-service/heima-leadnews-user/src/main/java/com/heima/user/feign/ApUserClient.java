package com.heima.user.feign;

import com.heima.apis.user.IApUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApUserClient implements IApUserClient {
    @Autowired
    private ApUserService apUserService;

    @Override
    @GetMapping("/api/v1/user/{id}")
    public ResponseResult getById(@PathVariable Integer id) {
        return ResponseResult.okResult(apUserService.getById(id));
    }
}
