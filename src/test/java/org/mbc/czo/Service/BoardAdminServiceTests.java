package org.mbc.czo.Service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.dto.BoardAdminDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminRequestDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminResponseDTO;
import org.mbc.czo.function.boardAdmin.repository.BoardAdminRepository;
import org.mbc.czo.function.boardAdmin.service.BoardAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class BoardAdminServiceTests {

    @Autowired
    private BoardAdminService boardAdminService;

    @Test
    public void testRegister(){
        log.info(boardAdminService.getClass().getName());

        BoardAdminDTO boardAdminDTO = BoardAdminDTO.builder()
                .title("샘플 제목")
                .content("샘플 내용")
                .writer("샘플 작성자")
                .build();

        Long bno = boardAdminService.register(boardAdminDTO);
        log.info("번호: " + bno);
    }

    @Test
    public void testModify(){

        BoardAdminDTO boardAdminDTO = BoardAdminDTO.builder()
                .bno(10L)
                .title("제목이 바뀝니다아아ㅏ")
                .content("내용이 바뀝니다아아")
                .build();
        boardAdminService.modify(boardAdminDTO);
    }

    @Test
    public void testDelete(){
        Long bno = 100L;
        boardAdminService.remove(bno);
    }

    @Test
    public void testList() {
        PageAdminRequestDTO pageAdminRequestDTO = PageAdminRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        PageAdminResponseDTO<BoardAdminDTO> responseDTO = boardAdminService.list(pageAdminRequestDTO);
        log.info(responseDTO);
    }

}
