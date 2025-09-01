package org.mbc.czo.function.boardAdmin.repository;

import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.domain.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardAdminLikeRepository extends JpaRepository<BoardLike, Long> {


    // 특정 게시물의 좋아요 수 조회
    int countByBoard(BoardAdmin board);

    void deleteByBoardAndUsername(BoardAdmin board, String username);

    @Query("SELECT bl FROM BoardLike bl WHERE bl.board.bno = :boardId AND bl.username = :username")
    Optional<BoardLike> findByBoardBnoAndUsername(@Param("boardId") Long bno, @Param("username") String username);



}
