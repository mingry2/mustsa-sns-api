package com.mutsasns.domain.dto.comment.delete;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentDeleteResponse {

	private String message;
	private Long id;

}
