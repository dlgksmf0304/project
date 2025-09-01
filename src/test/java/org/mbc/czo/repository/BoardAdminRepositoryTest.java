package org.mbc.czo.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.repository.BoardAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardAdminRepositoryTest {

    @Autowired
    private BoardAdminRepository boardAdminRepository;

    @Test
    public void tesInsert() {  // 테이블 내용 만들기
        IntStream.rangeClosed(1, 100).forEach(i -> {
            BoardAdmin boardAdmin = BoardAdmin.builder()
                    .title("제목: " + i)
                    .content("내용: " + i)
                    .writer("작성자: " + i)
                    .build();

            BoardAdmin result = boardAdminRepository.save(boardAdmin);
            log.info("번호: " + result.getBno());
        });
    }

    @Test
    public void testSelect() {  // 테이블 보기
        long bno = 100L;
        Optional<BoardAdmin> result = boardAdminRepository.findById(bno);
        BoardAdmin boardAdmin = result.orElseThrow();

        log.info(boardAdmin);
    }

    public void testUpdate() {  // 테이블 수정
        long bno = 100L;
        Optional<BoardAdmin> result = boardAdminRepository.findById(bno);
        BoardAdmin boardAdmin = result.orElseThrow();

        boardAdmin.change("update 제목..100","update 내용..100");
        boardAdminRepository.save(boardAdmin);
    }

    @Test
    public void testDelete() {  // 테이블 전체 삭제
        Long id = 1L;

        boardAdminRepository.deleteAll();
    }

    @Test
    public void testPaging(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<BoardAdmin> result = boardAdminRepository.findAll(pageable);

        log.info("총합 내용: " + result.getTotalElements());
        log.info("총합 페이지 수: " + result.getTotalPages());
        log.info("페이지 번호: " + result.getNumber());
        log.info("페이지 크기: " + result.getSize());

        List<BoardAdmin> todoList = result.getContent();

        todoList.forEach(boardAdmin -> log.info(boardAdmin));
    }

    @Test
    public void testSearch1(){
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());
        boardAdminRepository.search1(pageable);
    }

    @Test
    public void testSearchAll(){  // 전체 검색
        String[] types = {"t", "c", "w"};
        String keyword = "1";
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        Page<BoardAdmin> result = boardAdminRepository.searchAll(types, keyword, pageable);
    }


}

