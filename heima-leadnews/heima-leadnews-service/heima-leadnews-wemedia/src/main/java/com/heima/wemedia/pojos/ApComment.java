package com.heima.wemedia.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document("ap_comment")
public class ApComment implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private String id;
    /**
     * 评论人id
     */
    private Integer authorId;
    /**
     * 评论人昵称
     */
    private String authorName;
    /**
     * 文章或动态id
     */
    private Long entryId;
    /**
     * 频道ID
     */
    private Integer channelId;
    /**
     * 评论类型
     * 0 文章
     * 1 动态
     */
    private Integer type;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 作者头像
     */
    private String image;
    /**
     * 点赞
     */
    private Integer likes;
    /**
     *回复数
     */
    private Integer reply;
    /**
     * 文章标记
     * 0 普通评论
     * 1 热点评论
     * 2 推荐评论
     * 3 置顶评论
     * 4 精品评论
     * 5 大V 评论
     */
    private Integer flag;
    /**
     * 评论排列序号
     */
    private Integer ord;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 评论状态  0 关闭状态   1 正常状态
     */
    private Boolean status;
}
