package com.mutsasns.domain.dto.comment.create;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentCreateResponse {

	private Long id;
	private String comment;
	private String userName;
	private Long postId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;


}
