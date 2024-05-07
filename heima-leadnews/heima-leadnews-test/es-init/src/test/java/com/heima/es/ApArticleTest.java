package com.heima.es;

import com.alibaba.fastjson.JSON;
import com.heima.es.mapper.ApArticleMapper;
import com.heima.es.pojo.SearchArticleVo;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApArticleTest {

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ApArticleMapper apArticleMapper;
    /**
     * 注意：数据量的导入，如果数据量过大，需要分页导入
     * @throws Exception
     */
    @Test
    public void init() throws Exception {
        //1.查询数据库
        List<SearchArticleVo> searchArticleVos = apArticleMapper.loadArticleList();
        //2.导入数据
        BulkRequest bulk = new BulkRequest();
        for (SearchArticleVo searchArticleVo : searchArticleVos) {
            bulk.add(new IndexRequest("app_info_article")
                    .id(String.valueOf(searchArticleVo.getId()))
                    .source(JSON.toJSONString(searchArticleVo), XContentType.JSON));
        }
        client.bulk(bulk, RequestOptions.DEFAULT);
    }

}