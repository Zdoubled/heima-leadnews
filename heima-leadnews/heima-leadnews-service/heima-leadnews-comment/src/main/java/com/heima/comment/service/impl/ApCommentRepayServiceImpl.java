package com.heima.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.user.IApUserClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentRepay;
import com.heima.comment.pojos.ApCommentRepayLike;
import com.heima.comment.service.ApCommentRepayService;
import com.heima.model.comment.dtos.CommentRepayDto;
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

import java.util.Date;
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
        ApCommentRepay apCommentRepay = new ApCommentRepay();
        //用户id
        apCommentRepay.setAuthorId(user.getId());
        apCommentRepay.setCommentId(dto.getCommentId());
        //用户昵称
        ResponseResult result = apUserClient.getById(user.getId());
        if (!result.getCode().equals(200) || null == result.getData()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        String apUserStr = JSON.toJSONString(result.getData());
        ApUser apUser = JSON.parseObject(apUserStr, ApUser.class);
        apCommentRepay.setAuthorName(apUser.getName());
        //评论内容
        apCommentRepay.setContent(dto.getContent());
        //创建时间
        Date date = new Date();
        apCommentRepay.setCreatedTime(date);
        //更新时间
        apCommentRepay.setUpdatedTime(date);
        apCommentRepay.setLikes(0);//点赞数
        //4.保存mongo
        mongoTemplate.save(apCommentRepay);
        //5.评论回复数 +1
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        apComment.setReply(apComment.getReply() == null ? 1 : apComment.getReply() + 1);
        mongoTemplate.save(apComment);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult getByCommentId(CommentRepayDto dto) {
        //1.参数校验
        if (null == dto || null == dto.getCommentId()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
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
}
