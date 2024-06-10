package com.mutsasns.domain.dto.alarm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mutsasns.domain.entity.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AlarmResponse {

	private Long id;
	private AlarmType alarmType;
	private Long fromUserId;
	private Long targetId;
	private String text;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;

}
