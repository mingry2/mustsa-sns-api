package com.mutsasns.domain.dto.comment.modify;

import com.mutsasns.domain.entity.Comment;
import com.mutsasns.domain.entity.Post;
import com.mutsasns.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentModifyRequest {

	private String comment;

	// CommentModifyRequest -> Comment
	public Comment toEntity(User user, Post post) {
		return Comment.builder()
				.comment(this.comment)
				.user(user)
				.post(post)
				.build();
	}
}
