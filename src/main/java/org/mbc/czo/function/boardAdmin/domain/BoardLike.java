package org.mbc.czo.function.boardAdmin.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id") // FK 컬럼명 지정
    private BoardAdmin board;

    private String username; // 좋아요 누른 사용자

    // 기본 생성자 필요
    public BoardLike() {}

    public BoardLike(BoardAdmin board, String username) {
        this.board = board;
        this.username = username;
    }

    // getter / setter

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BoardAdmin getBoard() { return board; }
    public void setBoard(BoardAdmin board) { this.board = board; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

}
