package org.mbc.czo.function.boardAdmin.dto;


import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter

public class BoardAdminDTO {

    private Long bno;

    @NotEmpty
    @Size(min = 3, max =100)
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String writer;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private List<String> fileNames;

    private int viewCount;  // 조회 수

    private boolean notice;  // 공지 (공지는 블론 걸어서 하기)

    private int likeCount;      // 좋아요 수

    private boolean liked;  // 로그인 유저

    // 업로드 단계에서 받은 tempKey
    private String tempKey;

    public BoardAdminDTO(BoardAdmin entity) {  // 생성자
        this.bno = entity.getBno();
        this.title = entity.getTitle();
        this.writer = entity.getWriter();
        this.regDate = entity.getRegDate();
        this.modDate = entity.getModDate();
        this.viewCount = entity.getViewCount();
        this.notice = entity.isNotice();
    }

    }


