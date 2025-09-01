package org.mbc.czo.function.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = AuditingEntityListener.class)  // 감시용 클래스
@Getter  // 날짜 처리용
public class BaseAdminEntity {

    @CreatedDate // 생성일
    @Column(name="regDate", updatable = false)  // updatable = false 수정 금지라는 말
    private LocalDateTime regDate;

    @LastModifiedDate  // 수정일용
    @Column(name="modDate")  // 데이터베이스 필드명
    private LocalDateTime modDate;  // 수정일

}

