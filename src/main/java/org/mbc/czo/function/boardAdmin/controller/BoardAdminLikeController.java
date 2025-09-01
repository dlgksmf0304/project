package org.mbc.czo.function.boardAdmin.controller;

import org.mbc.czo.function.boardAdmin.service.BoardAdminLikeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/board")
public class BoardAdminLikeController {

    private final BoardAdminLikeService boardAdminLikeService;

    public BoardAdminLikeController(BoardAdminLikeService boardLikeService) {
        this.boardAdminLikeService = boardLikeService;
    }


    @PostMapping("/like/{bno}")
    public Map<String, Object> toggleLike(@PathVariable Long bno,
                                          @AuthenticationPrincipal UserDetails user) {
        if(user == null) throw new RuntimeException("로그인 필요");  // 회원만 좋아요 가능
        String username = user.getUsername();
        return boardAdminLikeService.toggleLike(bno, username); // { success, likeCount, liked }
    }
}
