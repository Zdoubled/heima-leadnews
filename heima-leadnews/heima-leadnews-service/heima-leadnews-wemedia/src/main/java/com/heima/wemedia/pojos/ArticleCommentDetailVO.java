package com.heima.wemedia.pojos;

import com.heima.model.comment.vos.ApCommentVO;
import lombok.Data;

import java.util.List;

@Data
public class ArticleCommentDetailVO{
    private ApCommentVO apComments;
    private List<ApCommentRepay> apCommentRepays;
}
