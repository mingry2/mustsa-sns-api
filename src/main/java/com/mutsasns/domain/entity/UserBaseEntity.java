package com.mutsasns.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass // [JPA] 부모 클래스는 테이블과 매핑하지 않고 부모 클래스를 상속받는 자식 클래스에게 매핑 정보만 제공
@EntityListeners(AuditingEntityListener.class)
public class UserBaseEntity {
    @CreatedDate // [jpa] Entity가 생성되어 저장될 때 시간 자동 저장
    @Column(updatable = false)  // updatable = false : column 수정 시 값 변경을 막는다.
    private LocalDateTime registeredAt; // 등록된 시간

    @LastModifiedDate // [jpa] 수정된 시간 정보를 자동으로 저장
    private LocalDateTime updateAt; // 수정된 시간
}
