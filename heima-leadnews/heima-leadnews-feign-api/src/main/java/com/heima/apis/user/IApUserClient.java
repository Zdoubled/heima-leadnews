package com.heima.apis.user;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "leadnews-user")
public interface IApUserClient {

    @GetMapping("/api/v1/user/{id}")
    public ResponseResult getById(@PathVariable Integer id);
}
