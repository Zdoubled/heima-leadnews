package com.heima.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.user.IApUserClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentRepay;
import com.heima.comment.pojos.ApCommentRepayLike;
import com.heima.comment.service.ApCommentRepayService;
import com.heima.model.comment.dtos.CommentRepayDto;
import com.heima.model.comment.dtos.CommentRepayLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.comment.vos.ApCommentRepayVO;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApCommentRepayServiceImpl implements ApCommentRepayService {
    @Autowired
    private IApUserClient apUserClient;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult insert(CommentRepaySaveDto dto) {
        //1.参数校验
        if (null == dto || null == dto.getCommentId() || null == dto.getContent()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断
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
        ResponseResult result = apUserClient.getById(user.getId());
        if (!result.getCode().equals(200) || null == result.getData()){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"用户不存在");
        }
        //用户昵称
        String apUserStr = JSON.toJSONString(result.getData());
        ApUser apUser = JSON.parseObject(apUserStr, ApUser.class);
        ApCommentRepay apCommentRepay = new ApCommentRepay();
        apCommentRepay.setAuthorName(apUser.getName());
        //用户id
        apCommentRepay.setAuthorId(user.getId());
        apCommentRepay.setCommentId(dto.getCommentId());
        //评论内容
        apCommentRepay.setContent(dto.getContent());
        Date date = new Date();
        //创建时间
        apCommentRepay.setCreatedTime(date);
        //更新时间
        apCommentRepay.setUpdatedTime(date);
        apCommentRepay.setLikes(0);//点赞数
        //4.保存mongo
        mongoTemplate.save(apCommentRepay);
        //5.评论回复数 +1
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        apComment.setReply(apComment.getReply() + 1);
        mongoTemplate.save(apComment);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult getByCommentId(CommentRepayDto dto) {
        //1.参数校验
        if (null == dto || null == dto.getCommentId()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("查询评论最小时间参数为:{}", format.format(dto.getMinDate()));
        //2.条件查询
        if (dto.getSize() == null || dto.getSize() == 0){
            dto.setSize(10);
        }
        Query query = Query.query(Criteria.where("commentId").is(dto.getCommentId()).and("createdTime").lt(dto.getMinDate()))
                .with(Sort.by(Sort.Direction.DESC, "createdTime"))
                .limit(dto.getSize());
        List<ApCommentRepay> apCommentRepays = mongoTemplate.find(query, ApCommentRepay.class);
        List<ApCommentRepayVO> apCommentRepayVOS = apCommentRepays.stream().map(apCommentRepay -> {
            ApCommentRepayVO apCommentRepayVO = new ApCommentRepayVO();
            BeanUtils.copyProperties(apCommentRepay, apCommentRepayVO);
            return apCommentRepayVO;
        }).collect(Collectors.toList());
        log.info("查询评论结果有:{}条", apCommentRepayVOS.size());
        //3.是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (null == user){
            return ResponseResult.okResult(apCommentRepayVOS);
        }
        //3.1判断是否点赞
        apCommentRepayVOS.forEach(apCommentRepayVO -> {
            Query query1 = Query.query(Criteria.where("commentRepayId").is(apCommentRepayVO.getId()).and("authorId").is(user.getId()));
            ApCommentRepayLike one = mongoTemplate.findOne(query1, ApCommentRepayLike.class);
            if (one != null){
                apCommentRepayVO.setOperation((short)0);
            }
        });
        //4.封装返回
        return ResponseResult.okResult(apCommentRepayVOS);
    }

    @Override
    public ResponseResult like(CommentRepayLikeDto dto) {
        //1.参数校验
        if (null == dto || null == dto.getCommentRepayId() || null == dto.getOperation() || dto.getOperation() > 1 || dto.getOperation() < 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (null == user){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //3.点赞操作，并新增点赞数
        ApCommentRepay one = mongoTemplate.findById(dto.getCommentRepayId(), ApCommentRepay.class);
        if (dto.getOperation() == 0){
            //新增点赞
            ApCommentRepayLike apCommentRepayLike = new ApCommentRepayLike();
            apCommentRepayLike.setCommentRepayId(dto.getCommentRepayId());
            apCommentRepayLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentRepayLike);
            //点赞数 +1
            one.setLikes(one.getLikes() + 1);
            mongoTemplate.save(one);
        }else {
            //取消点赞
            Query query = Query.query(Criteria.where("commentRepayId").is(dto.getCommentRepayId()).and("authorId").is(user.getId()));
            mongoTemplate.remove(query, ApCommentRepayLike.class);
            //点赞数 -1
            int count = one.getLikes() - 1;
            count = count < 1 ? 0 : count;
            one.setLikes(count);
            mongoTemplate.save(one);
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("likes", one.getLikes());
        return ResponseResult.okResult(result);
    }
}
