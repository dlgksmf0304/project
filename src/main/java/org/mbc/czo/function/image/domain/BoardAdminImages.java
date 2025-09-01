package org.mbc.czo.function.image.domain;

import jakarta.persistence.*;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;

@Entity

@Table(name="boardAdminImages")
public class BoardAdminImages extends BaseImage {

    // 아직 board 없을 때 임시 그룹핑 키
    // 게시글 생성 전은 boardAdmin = null
    private String tempKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardAdmin boardAdmin;

    public BoardAdminImages() {}

    public BoardAdminImages(String originalFileName, String storedFileName, String uploadPath, String tempKey, BoardAdmin boardAdmin) {
        super(originalFileName, storedFileName, uploadPath);
        this.tempKey = tempKey;
        this.boardAdmin = boardAdmin;
    }


    public void changeBoardAdmin(BoardAdmin boardAdmin) {
        this.boardAdmin = boardAdmin;
        this.tempKey = null; // 실제 게시글 연결되면 tempKey 제거
    }
}

