package org.mbc.czo.function.boarduser.domain;

import jakarta.persistence.*;
import lombok.*;
import org.mbc.czo.function.common.entity.BaseAdminEntity;

import java.time.LocalDateTime;
import java.util.List;

//1

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board extends BaseAdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동완성 번호
    private Long bno; //게시물번호

    @Column(length = 200, nullable = false)
    private String title; //제목

    @Column(length = 2000, nullable = false)
    private String content; // 내용

    @Column(length = 50, nullable = false)
    private String writer;    //작성자

    @Column
    private String category;



    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true) // 문의글이 사라지면 답변도 모두 삭제
    private List<Answer> answerList; // 문의글에 대한 답변글 리스트형식


    public void change(String title, String content) { //제목,내용 수정 메서드
        this.title = title;
        this.content = content;
    }


}
