package org.mbc.czo.function.boarduser.service;

import org.mbc.czo.function.boarduser.dto.PageRequestDTO;
import org.mbc.czo.function.boarduser.dto.PageResponseDTO;
import org.mbc.czo.function.boarduser.dto.ReplyDTO;

public interface ReplyService {

    Long register(ReplyDTO replyDTO);

    ReplyDTO read(Long rno);

    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);

    void modify(ReplyDTO replyDTO);

    void remove(Long rno);
}
