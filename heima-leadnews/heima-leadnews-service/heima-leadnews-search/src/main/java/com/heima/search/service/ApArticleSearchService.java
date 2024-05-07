package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

import java.io.IOException;

public interface ApArticleSearchService {

    /**
     * 搜索
     * @param dto
     * @return
     * @throws IOException
     */
    public ResponseResult search(UserSearchDto dto) throws IOException;
}
