package com.mutsasns.domain.entity;

import com.mutsasns.domain.dto.comment.create.CommentCreateResponse;
import com.mutsasns.domain.dto.comment.modify.CommentModifyResponse;
import lombok.*;
import org.hibernate.annotations.Where;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
//@SQLDelete(sql = "UPDATE comment SET deleted_at = current_timestamp WHERE comment_id = ?") --> 외래키 참조 에러가 계속남
@Where(clause = "deleted_at is NULL")
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public CommentCreateResponse toCreateResponse(){
        return CommentCreateResponse.builder()
                .id(this.getCommentId())
                .comment(this.getComment())
                .userName(this.getUser().getUserName())
                .postId(this.getPost().getPostId())
                .createdAt(getCreatedAt())
                .build();
    }

    public CommentModifyResponse toModifyResponse(){
        return CommentModifyResponse.builder()
                .id(this.getCommentId())
                .comment(this.getComment())
                .userName(this.getUser().getUserName())
                .postId(this.getPost().getPostId())
                .createdAt(getCreatedAt())
                .lastModifiedAt(getLastModifiedAt())
                .build();
    }

}
