package org.mbc.czo.function.boardAdmin.service;

import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.domain.BoardLike;
import org.mbc.czo.function.boardAdmin.repository.BoardAdminLikeRepository;
import org.mbc.czo.function.boardAdmin.repository.BoardAdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BoardLikeServiceImpl implements BoardAdminLikeService {

    private final BoardAdminRepository boardAdminRepository;
    private final BoardAdminLikeRepository boardAdminLikeRepository;

    public BoardLikeServiceImpl(BoardAdminRepository boardAdminRepository, BoardAdminLikeRepository boardAdminLikeRepository) {
        this.boardAdminRepository = boardAdminRepository;
        this.boardAdminLikeRepository = boardAdminLikeRepository;
    }

    @Override
    @Transactional
    public Map<String, Object> toggleLike(Long boardId, String username) {

        BoardAdmin board = boardAdminRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다"));

        Optional<BoardLike> existing = boardAdminLikeRepository.findByBoardBnoAndUsername(boardId, username);
        boolean liked;

        if(existing.isPresent()) {
            boardAdminLikeRepository.delete(existing.get());
            liked = false;
        } else {
            BoardLike like = new BoardLike();
            like.setBoard(board);
            like.setUsername(username);
            boardAdminLikeRepository.save(like);
            liked = true;
        }

        int likeCount = boardAdminLikeRepository.countByBoard(board);
        board.setLikeCount(likeCount);
        boardAdminRepository.save(board);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("liked", liked);
        result.put("likeCount", likeCount);

        return result;
    }

    @Override
    public boolean isLiked(Long bno, String username) {
        return boardAdminLikeRepository.findByBoardBnoAndUsername(bno, username).isPresent();
    }

    @Override
    public int countLikes(BoardAdmin board) {
        return boardAdminLikeRepository.countByBoard(board);
    }
}
