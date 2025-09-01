package org.mbc.czo.function.boarduser.search;
//1
import org.mbc.czo.function.boarduser.domain.Board;
import org.mbc.czo.function.boarduser.dto.BoardAllList;
import org.mbc.czo.function.boarduser.dto.BoardDTO;
import org.mbc.czo.function.boarduser.dto.ReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {

    Page<Board> search(Pageable pageable);

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

    Page<ReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable);

    Page<BoardAllList> searchWithAll(String[] types, String keyword, Pageable pageable);
}
