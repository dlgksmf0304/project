package org.mbc.czo.function.boarduser.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
//1
@Getter
@ToString
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;

    private int start; //시작번호
    private int end; //끝번호

    private boolean prev; //이전page
    private boolean next; //다음page

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO,List<E> dtoList, int total) {

        if(total <= 0){
            return; // 게시물없으면 돌아가셈
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.end = (int)(Math.ceil(this.page / 10.0)) * 10 ;
        this.start = this.end - 9;

        int last = (int)(Math.ceil((total/(double)size)));

        this.end = end > last ? last : end ;

        this.prev = this.start >1;
        this.next = total > this.end * this.size;
    }
}
