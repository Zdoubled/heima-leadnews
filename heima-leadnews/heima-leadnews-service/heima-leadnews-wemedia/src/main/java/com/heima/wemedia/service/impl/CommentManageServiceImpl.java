package com.heima.wemedia.service.impl;

import com.heima.apis.Article.IArticleClient;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.comment.vos.ApCommentVO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ArticleCommentDetailDto;
import com.heima.model.wemedia.dtos.ArticleCommentDto;
import com.heima.model.wemedia.dtos.ArticleCommentStatusDto;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.pojos.*;
import com.heima.wemedia.service.CommentManageService;
import com.heima.wemedia.service.WmUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentManageServiceImpl implements CommentManageService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private WmUserService wmUserService;
    @Autowired
    private IArticleClient articleClient;
    @Override
    public ResponseResult removeCommentByCommentId(String commentId) {
        //1.参数校验
        if (StringUtils.isBlank(commentId)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论id不能为空");
        }
        //2.删除评论及点赞信息
        mongoTemplate.remove(Query.query(Criteria.where("id").is(commentId)), ApComment.class);
        mongoTemplate.remove(Query.query(Criteria.where("commentId").is(commentId)), ApCommentLike.class);
        //3.删除评论所有回复及回复点赞信息
        //根据回复id删除所有点赞信息
        mongoTemplate.find(Query.query(Criteria.where("commentId").is(commentId)), ApCommentRepay.class).forEach(apCommentRepay -> {
            mongoTemplate.remove(Query.query(Criteria.where("commentRepayId").is(apCommentRepay.getId())), ApCommentRepayLike.class);
        });
        //删除评论所有回复
        mongoTemplate.remove(Query.query(Criteria.where("commentId").is(commentId)), ApCommentRepay.class);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult removeCommentRepayByCommentRepayId(String commentRepayId) {
        //1.参数校验
        if (StringUtils.isBlank(commentRepayId)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论回复id不能为空");
        }
        //2.删除回复点赞信息
        mongoTemplate.remove(Query.query(Criteria.where("commentRepayId").is(commentRepayId)), ApCommentRepayLike.class);
        //3.删除回复
        mongoTemplate.remove(Query.query(Criteria.where("id").is(commentRepayId)), ApCommentRepay.class);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult saveCommentRepay(CommentRepaySaveDto dto) {
        //1.参数校验
        if (dto == null || StringUtils.isBlank(dto.getCommentId()) || StringUtils.isBlank(dto.getContent())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (dto.getContent().length() > 140){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论内容不能超过140个");
        }
        //2.补全参数
        ApCommentRepay repay = new ApCommentRepay();
        repay.setCommentId(dto.getCommentId());
        repay.setContent(dto.getContent());
        repay.setLikes(0);
        WmUser user = WmThreadLocalUtil.getUser();
        user = wmUserService.getById(user.getId());
        repay.setAuthorId(user.getId());
        repay.setAuthorName(user.getName());
        Date date = new Date();
        repay.setCreatedTime(date);
        repay.setUpdatedTime(date);
        //2.保存评论回复
        mongoTemplate.save(repay);
        //3.评论回复数 +1
        ApComment comment = mongoTemplate.findOne(Query.query(Criteria.where("id").is(dto.getCommentId())), ApComment.class);
        if (comment != null){
            comment.setReply(comment.getReply() + 1);
            mongoTemplate.save(comment);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult updateCommentStatus(ArticleCommentStatusDto dto) {
        //1.参数校验
        if (dto == null || dto.getArticleId() == null || dto.getOperation() == null || dto.getOperation() > 1 || dto.getOperation() < 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.更新文章评论状态 0 关闭 1 开启
        ResponseResult result = articleClient.updateArticleCommentStatus(dto);
        if (!result.getCode().equals(200)){
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"跟新文章评论功能失败");
        }
        //若为关闭，删除所有评论
        if (dto.getOperation() == 0){
            mongoTemplate.find(Query.query(Criteria.where("entryId").is(dto.getArticleId())), ApComment.class).forEach(apComment -> {
                mongoTemplate.find(Query.query(Criteria.where("commentId").is(apComment.getId())), ApCommentRepay.class).forEach(apCommentRepay -> {
                    mongoTemplate.remove(Query.query(Criteria.where("commentRepayId").is(apCommentRepay.getId())), ApCommentRepayLike.class);
                });
                mongoTemplate.remove(Query.query(Criteria.where("commentId").is(apComment.getId())), ApCommentLike.class);
            });
            mongoTemplate.remove(Query.query(Criteria.where("entryId").is(dto.getArticleId())), ApComment.class);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult findNewsComments(ArticleCommentDto dto) {
        //1.参数校验
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.补全参数
        dto.checkParam();
        WmUser user = WmThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        dto.setWmUserId(user.getId());
        //3.调用文章服务接口
        return articleClient.findNewsComments(dto);
    }

    @Override
    public ResponseResult list(ArticleCommentDetailDto dto) {
        //1.参数校验
        if(null == dto || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        //2.分页查询
        ArrayList<ArticleCommentDetailVO> detailVOS = new ArrayList<>();
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()));
        query.with(PageRequest.of(dto.getPage()-1,dto.getSize()));
        query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        mongoTemplate.find(query, ApComment.class).forEach(apComment -> {
            ArticleCommentDetailVO detailVO = new ArticleCommentDetailVO();
            ApCommentVO apCommentVO = new ApCommentVO();
            BeanUtils.copyProperties(apComment, apCommentVO);
            WmUser user = WmThreadLocalUtil.getUser();
            ApCommentLike one = mongoTemplate.findOne(Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(user.getId())), ApCommentLike.class);
            apCommentVO.setOperation((short) (null == one ? 1 : 0));
            detailVO.setApComments(apCommentVO);


            List<ApCommentRepay> repays = mongoTemplate.find(Query.query(Criteria.where("commentId").is(apComment.getId())).with(Sort.by(Sort.Direction.DESC, "createdTime")), ApCommentRepay.class);
            detailVO.setApCommentRepays(repays);
            detailVOS.add(detailVO);
        });
        //3.结果返回
        return ResponseResult.okResult(detailVOS);
    }

    @Override
    public ResponseResult like(CommentLikeDto dto) {
        //1.参数校验
        if (null == dto || dto.getCommentId() == null || dto.getOperation() == null || dto.getOperation() < 0 || dto.getOperation() > 1){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断用户是否登录
        WmUser wmUser = WmThreadLocalUtil.getUser();
        if (null == wmUser){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //3.点赞操作
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        if (apComment == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论不存在");
        }

        Query query = Query.query(Criteria.where("authorId").is(wmUser.getId()).and("commentId").is(dto.getCommentId()));
        ApCommentLike apCommentLike = mongoTemplate.findOne(query, ApCommentLike.class);
        if (dto.getOperation() == 0){
            //查询是否已经点赞
            if (apCommentLike != null){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已点过赞");
            }
            apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(dto.getCommentId());
            apCommentLike.setAuthorId(wmUser.getId());
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
}
