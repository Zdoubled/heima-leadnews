package com.heima.comment.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("ap_comment_repay")
public class ApCommentRepay {
    private String id;
    private Integer authorId;
    private String authorName;
    private String commentId;
    private String content;
    private Date createdTime;
    private Integer likes;
    private Date updatedTime;
}
