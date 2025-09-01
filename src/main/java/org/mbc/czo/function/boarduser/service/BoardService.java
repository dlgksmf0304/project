package org.mbc.czo.function.boarduser.service;

//1

import org.mbc.czo.function.boarduser.dto.BoardAllList;
import org.mbc.czo.function.boarduser.dto.BoardDTO;
import org.mbc.czo.function.boarduser.dto.PageRequestDTO;
import org.mbc.czo.function.boarduser.dto.PageResponseDTO;
import org.mbc.czo.function.boarduser.dto.ReplyCountDTO;
import org.springframework.stereotype.Service;

@Service
public interface BoardService {

    Long register(BoardDTO boardDTO); //등록처리

    BoardDTO readOne(Long bno); //조회 처리

    void modify(BoardDTO boardDTO); //수정 처리

    void remove(Long bno); //삭제처리

    PageResponseDTO<BoardDTO> list (PageRequestDTO pageRequestDTO);

    PageResponseDTO<ReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

    PageResponseDTO<BoardAllList> listWithAll(PageRequestDTO pageRequestDTO);
}
