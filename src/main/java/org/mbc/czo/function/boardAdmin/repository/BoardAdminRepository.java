package org.mbc.czo.function.boardAdmin.repository;

import org.mbc.czo.function.boardAdmin.Search.BoardAdminSearch;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardAdminRepository extends JpaRepository<BoardAdmin, Long>, BoardAdminSearch {

    Page<BoardAdmin> findByTitleContainingOrderByBnoDesc(String keyword, Pageable pageable); // 제목에 특정 키워드가 존재하는 게시글 찾기

    @Query("select b from BoardAdmin b where b.title like concat('%', : keyword, '%')")
        // 위에 거를 쿼리로 수정
    Page<BoardAdmin> findKeyword(String keyword, Pageable pageable);

    @Query(value = "select now()", nativeQuery = true)
        // 날짜 변화해 주는 쿼리
    String getTime();

    @Modifying  // 데이터베이스 변경시 쓰는 쿼리 (데이터베이스에 조회 수 추가해서)
    @Query("update BoardAdmin b set b.viewCount = b.viewCount + 1 where b.bno = :bno")
    void increaseViewCount(Long bno);


    @Query(value = "SELECT b FROM BoardAdmin b ORDER BY b.notice DESC, b.bno DESC",  // 공지 박는 쿼리
            countQuery = "SELECT COUNT(b) FROM BoardAdmin b")
    Page<BoardAdmin> findAllOrderByNotice(Pageable pageable);

    @Query("SELECT b FROM BoardAdmin b ORDER BY b.notice DESC, b.bno DESC")
    Page<BoardAdmin> findAllByNoticeFirst(Pageable pageable);  // 이것도 공지 박는 쿼리

    Long bno(Long bno);
}




