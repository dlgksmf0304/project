package org.mbc.czo.function.boardAdmin.service;

import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;

import java.util.Map;

public interface BoardAdminLikeService {

    Map<String, Object> toggleLike(Long boardId, String username);

    boolean isLiked(Long bno, String username);

    int countLikes(BoardAdmin board);
}
