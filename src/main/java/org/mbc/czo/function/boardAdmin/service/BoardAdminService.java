package org.mbc.czo.function.boardAdmin.service;

import jakarta.validation.Valid;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.dto.BoardAdminDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminRequestDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BoardAdminService {

    Long register(BoardAdminDTO boardAdminDTO);  // 글 작성

    BoardAdminDTO readOne(Long bno);  // 상세 보기

    void modify(BoardAdminDTO dto); // 수정하기

    void remove(Long bno);

    PageAdminResponseDTO<BoardAdminDTO> list(PageAdminRequestDTO pageAdminRequestDTO);

    BoardAdminDTO get(Long bno);  // 수정할 때 쓰는 서비스

    BoardAdmin getBoard(Long bno);  // 조회 수 추가


    List<BoardAdminDTO> getBoardList(Pageable pageable);

    void save(@Valid BoardAdminDTO boardAdminDTO);






    /*void save(String title, String content);*/
}



