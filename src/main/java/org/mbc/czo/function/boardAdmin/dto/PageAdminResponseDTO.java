package org.mbc.czo.function.boardAdmin.dto;

import lombok.*;
import org.springframework.data.domain.Sort;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageAdminResponseDTO<E> {

    private int page;   // 현재 페이지,
    private int size;   // 페이지당 게시물 수
    private int total;  //  총 게시물

    private int start;  // 시작 페이지
    private int end;  // 끝 페이지

    private boolean prev;  // 이전 페이지 존재 여부
    private boolean next;  // 다음 페이지 존재 여부

    private List<E> dtoList;

    private PageAdminRequestDTO pageAdminRequestDTO;

    @Builder(builderMethodName = "withAll")
    public PageAdminResponseDTO(PageAdminRequestDTO pageAdminRequestDTO, List<E> dtoList, int total){

        // 🔥 무조건 기본값들부터 설정
        this.page = pageAdminRequestDTO.getPage();
        this.size = pageAdminRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList != null ? dtoList : new ArrayList<>();
        this.pageAdminRequestDTO = pageAdminRequestDTO;

        // total이 0이면 기본 페이지만 설정
        if (total <= 0) {
            this.start = 1;
            this.end = 1;
            this.prev = false;
            this.next = false;
            return;
        }

        makePageList(); // 정상적인 페이지 계산
    }



        public void makePageList () {
            // 현재 페이지 번호 (1부터 시작)
            int currentPage = Math.max(this.page, 1);

            // 전체 페이지 수 계산
            int totalPages = (int) Math.ceil(this.total / (double) this.size);
            if (totalPages < 1) {
                totalPages = 1;
            }

            // 화면에 보여줄 페이지 블록의 마지막 번호 계산
            this.end = (int) (Math.ceil(currentPage / 10.0)) * 10;

            // 화면에 보여줄 페이지 블록의 시작 번호 계산
            this.start = this.end - 9;
            if (this.start < 1) {
                this.start = 1;
            }

            // 마지막 페이지 블록이 전체 페이지 수를 초과하는 경우
            if (this.end > totalPages) {
                this.end = totalPages;
            }

            // 이전/다음 페이지 존재 여부
            this.prev = this.start > 1;
            this.next = this.end < totalPages;
        }



    }

