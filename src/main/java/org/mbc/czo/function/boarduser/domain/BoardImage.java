package org.mbc.czo.function.boarduser.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

//1

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class BoardImage implements Comparable<BoardImage> {

    @Id
    private String bid;
    private String fileName;
    private int ord;

    @ManyToOne // 하나의 게시물에 여러개 이미지 첨부
    private Board board;

    @Override
    public int compareTo(BoardImage other) {
        return 0;
    }

    public void changeBoard(Board board) { this.board = board;
    }
}
