package org.mbc.czo.function.boarduser.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyCountDTO {

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;

    private Long replyCount;
}
