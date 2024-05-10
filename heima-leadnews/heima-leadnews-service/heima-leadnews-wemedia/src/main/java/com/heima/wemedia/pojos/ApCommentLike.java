package com.heima.wemedia.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("ap_comment_like")
public class ApCommentLike {
    private String id;
    private Integer authorId;
    private String commentId;
}
