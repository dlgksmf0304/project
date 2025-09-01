package org.mbc.czo.function.boarduser.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//1
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

    @CreatedDate
    @Column(name = "credate", updatable = false) // 생성일 수정 금지
    private LocalDateTime createdAt; // 등록일

    @LastModifiedBy
    @Column(name = "moddate")
    private LocalDateTime modifiedAt; //수정일
}
