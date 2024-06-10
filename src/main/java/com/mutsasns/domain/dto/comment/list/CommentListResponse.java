package com.mutsasns.domain.dto.comment.list;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mutsasns.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentListResponse {

	private Long id;
	private String comment;
	private String userName;
	private Long postId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;

	// Page<Comment> -> Page<CommentListResponses>
	public static Page<CommentListResponse> toResponse(Page<Comment> commentList) {
		Page<CommentListResponse> commentListResponses = commentList.map(
				comment1 -> CommentListResponse.builder()
						.id(comment1.getCommentId())
						.comment(comment1.getComment())
						.userName(comment1.getUser().getUserName())
						.postId(comment1.getPost().getPostId())
						.createdAt(comment1.getCreatedAt())
						.build());

		return commentListResponses;
	}
}
