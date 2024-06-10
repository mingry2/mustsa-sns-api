package com.mutsasns.domain.entity;

import com.mutsasns.domain.dto.alarm.AlarmResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
//@Where(clause = "deleted_at is NULL")
//@SQLDelete(sql = "UPDATE alarm SET deleted_at = current_timestamp WHERE id = ?")
public class Alarm extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알람 id

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType; // 알람 타입(NEW_COMMENT_ON_POST , NEW_LIKE_ON_POST)

    private Long fromUserId; // 알림을 발생시킨 user id
    private Long targetId; // 알림이 발생된 post id
    private String text; // alarmType 따라 string 필드에 담아 줄 수 있도록 필드를 선언합니다.

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Alarm -> AlarmResponse
    public AlarmResponse toResponse() {
        return AlarmResponse.builder()
                .id(id)
                .alarmType(alarmType)
                .fromUserId(fromUserId)
                .targetId(targetId)
                .text(alarmType.getAlarmText())
                .createdAt(getCreatedAt())
                .build();

    }

    // 알람 저장 메소드
    public static Alarm addAlarm(AlarmType alarmType, User user, Post post) {
        return Alarm.builder()
                .alarmType(alarmType)
                .fromUserId(user.getUserId())
                .targetId(post.getPostId())
                .text(alarmType.getAlarmText())
                .user(post.getUser())
                .build();
    }

}
