package org.mbc.czo.function.boardAdmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardAdminAllListDTO {

    private Long bno;

    private String title;

    private String content;

    private LocalDateTime regDate;

    private Long replyCount;

    private List<BoardAdminImageDTO> boardImages;

}
