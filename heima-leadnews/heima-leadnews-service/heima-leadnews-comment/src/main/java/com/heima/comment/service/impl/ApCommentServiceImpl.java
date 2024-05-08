package com.heima.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.Article.IArticleClient;
import com.heima.apis.user.IApUserClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentLike;
import com.heima.comment.service.ApCommentService;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.comment.vos.ApCommentVO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApCommentServiceImpl implements ApCommentService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IArticleClient articleClient;
    @Autowired
    private IApUserClient apUserClient;
    @Override
    public ResponseResult insert(CommentSaveDto dto) {
        //1.参数校验
        if (null == dto || null == dto.getArticleId() || null == dto.getContent()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断
        //判断文章是否开启评论
        if (checkAble(dto.getArticleId())){
            return ResponseResult.errorResult(403, "文章未开启评论");
        }
        //判断评论是否超过140字
        if (dto.getContent().length() > 140){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //TODO 判断内容是否违规

        //判断用户是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (null == user) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //3.数据补全
        ApComment apComment = new ApComment();
        apComment.setAuthorId(user.getId());//用户id
        //用户昵称
        ResponseResult result = apUserClient.getById(user.getId());
        if (!result.getCode().equals(200) || null == result.getData()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        String apUserStr = JSON.toJSONString(result.getData());
        ApUser apUser = JSON.parseObject(apUserStr, ApUser.class);
        apComment.setAuthorName(apUser.getName());//评论作者昵称
        apComment.setContent(dto.getContent());//评论内容
        apComment.setCreatedTime(new Date());//创建时间
        apComment.setEntryId(dto.getArticleId());//文章或动态id
        apComment.setFlag(0);//评论级别  0默认为普通评论
        apComment.setType(0);//评论类型 0文章 1动态
        apComment.setReply(0);//回复数
        apComment.setLikes(0);//点赞数

        //4.保存mongo
        mongoTemplate.save(apComment);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 查询所有评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult getByArticleId(CommentDto dto) {
        //1.参数校验
        if (null == dto || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.条件查询
        //默认十条
        int size = 10;
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("createdTime").lt(dto.getMinDate()));
        query.with(Sort.by(Sort.Direction.DESC, "createdTime")).limit(size);
        List<ApComment> comments = mongoTemplate.find(query, ApComment.class);
        List<ApCommentVO> apCommentVOS = comments.stream().map(apComment -> {
            ApCommentVO apCommentVO = new ApCommentVO();
            BeanUtils.copyProperties(apComment, apCommentVO);
            return apCommentVO;
        }).collect(Collectors.toList());
        log.info("查询评论结果有：{}条", apCommentVOS.size());
        //3.用户未登录，直接封装返回
        ApUser user = AppThreadLocalUtil.getUser();
        if (null == user){
            return ResponseResult.okResult(apCommentVOS);
        }
        //已经登录，查询用户是否点赞该评论
        apCommentVOS.forEach(apCommentVO -> {
            Query query1 = Query.query(Criteria.where("commentId").is(apCommentVO.getId()).and("authorId").is(user.getId()));
            ApCommentLike one = mongoTemplate.findOne(query1, ApCommentLike.class);
            if (null != one){
                apCommentVO.setOperation((short) 0);
            }
        });
        return ResponseResult.okResult(apCommentVOS);
    }

    /**
     * 点赞评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(CommentLikeDto dto) {
        //1.参数校验
        if (null == dto || dto.getCommentId() == null || dto.getOperation() == null || dto.getOperation() < 0 || dto.getOperation() > 1){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断用户是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (null == user){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //3.点赞操作
        Query query = Query.query(Criteria.where("authorId").is(user.getId()).and("commentId").is(dto.getCommentId()));
        ApCommentLike apCommentLike = mongoTemplate.findOne(query, ApCommentLike.class);

        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        if (apComment == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论不存在");
        }
        if (dto.getOperation() == 0){
            //查询是否已经点赞
            if (apCommentLike != null){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已点过赞");
            }
            apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(dto.getCommentId());
            apCommentLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentLike);
            //评论点赞数+1
            apComment.setLikes(apComment.getLikes() + 1);
            mongoTemplate.save(apComment);
        }else{
            //查询是否是未点赞
            if (apCommentLike == null){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "未点过赞");
            }
            mongoTemplate.remove(apCommentLike);
            //评论点赞数-1
            int count = apComment.getLikes() - 1;
            count = count < 1 ? 0 : count;
            apComment.setLikes(count);
            mongoTemplate.save(apComment);
        }
        //4.封装返回
        HashMap<String, Object> result = new HashMap<>();
        result.put("likes", apComment.getLikes());
        return ResponseResult.okResult(result);
    }

    private boolean checkAble(Long articleId) {
        ResponseResult result = articleClient.getApArticleConfigByArticleId(articleId);
        if (!result.getCode().equals(200) || null == result.getData()){
            return true;
        }
        String configStr = JSON.toJSONString(result.getData());
        ApArticleConfig apArticleConfig = JSON.parseObject(configStr, ApArticleConfig.class);
        if (apArticleConfig.getIsComment()){
            return false;
        }
        return true;
    }
}
