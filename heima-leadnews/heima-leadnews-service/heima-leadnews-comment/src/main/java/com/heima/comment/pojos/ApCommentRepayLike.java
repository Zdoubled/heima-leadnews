package com.heima.comment.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("ap_comment_repay_like")
public class ApCommentRepayLike {
    private String id;
    private String commentId;
    private Integer authorId;
}
