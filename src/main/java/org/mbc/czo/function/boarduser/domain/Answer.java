package org.mbc.czo.function.boarduser.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
//1
@Entity
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bno;

    @Column(length = 2000, nullable = false)
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne //하나 게시글에 여러 답변글 어노테이션
    private Board board;
}
