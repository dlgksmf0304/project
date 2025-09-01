package org.mbc.czo.function.boarduser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
//1
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardAllList {

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;
    private Long replyCount;
    private int viewCount;
    private String category;
   // private List<BoardImageDTO> boardImages;
}
