package org.mbc.czo.function.boarduser.repository;

//1

import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boarduser.domain.Board;
import org.mbc.czo.function.boarduser.search.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {


    Page<Board> findByTitleContainingOrderByBnoDesc(String keyword, Pageable pageable); // 제목에 특정 키워드가 존재하는 게시글 찾기

    @Query("select b from Board b where b.title like concat('%', : keyword, '%')")
        // 위에 거를 쿼리로 수정
    Page<Board> findKeyword(String keyword, Pageable pageable);

    @Query(value = "select now()", nativeQuery = true)
    String getTime();

    @Query("SELECT b FROM Board b ORDER BY b.bno DESC, b.regDate DESC")
    Page<Board> findAllOrderByNotice(Pageable pageable);
}
