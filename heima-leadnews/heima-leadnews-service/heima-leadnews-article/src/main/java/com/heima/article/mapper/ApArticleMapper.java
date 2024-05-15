package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.ArticleCommentVO;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    public List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDto dto, @Param("type") Short type);

    public List<ApArticle> findArticleListByLast5days(@Param("dayParam") Date dayParam);

    public List<ArticleCommentVO> findNewsComments(@Param("dto") ArticleCommentDto dto);

    public int findNewsCommentsCount(@Param("dto") ArticleCommentDto dto);

    @ResultMap("resultMap2")
    public Map<String, Object> queryByDateAndId(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate,@Param("id") Integer id);
}
