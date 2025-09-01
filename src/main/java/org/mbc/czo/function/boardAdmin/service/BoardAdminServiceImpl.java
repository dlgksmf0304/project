package org.mbc.czo.function.boardAdmin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.dto.BoardAdminDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminRequestDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminResponseDTO;
import org.mbc.czo.function.boardAdmin.repository.BoardAdminRepository;
import org.mbc.czo.function.image.domain.BoardAdminImages;
import org.mbc.czo.function.image.repository.BoardAdminImageJpaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardAdminServiceImpl implements BoardAdminService {

    private final ModelMapper modelMapper;
    private final BoardAdminRepository boardAdminRepository;
    private final BoardAdminImageJpaRepository boardAdminImageJpaRepository;

    /*이현우 수정 */
    /*register는 게시글 등록 전 단계이므로 bno가 따로 없다. tempKey를 활용해 bno를 대체*/
    /*register가 완료되면 */
    @Override
    public Long register(BoardAdminDTO boardAdminDTO) {
        BoardAdmin boardAdmin = modelMapper.map(boardAdminDTO, BoardAdmin.class);

        // tempKey 기반 이미지 연결
        String tempKey = boardAdminDTO.getTempKey();
        if (tempKey != null) {
            List<BoardAdminImages> tempImages = boardAdminImageJpaRepository.findByTempKey(tempKey);

            for (BoardAdminImages image : tempImages) {
                // 게시글 연결
                // tempKey 제거
                image.changeBoardAdmin(boardAdmin);
            }

            // 게시글 엔티티의 images 리스트에 추가
            boardAdmin.getImages().addAll(tempImages);

            boardAdminImageJpaRepository.saveAll(tempImages);
        }
        Long bno = boardAdminRepository.save(boardAdmin).getBno();

        return bno;
    }

    /*이현우 수정 */
    /*boardAdminDTO에 추가된 images set작업 추가*/
    @Override
    public BoardAdminDTO readOne(Long bno) {
        BoardAdmin board = boardAdminRepository.findById(bno)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // 조회수 증가
        board.increaseViewCount();  // 엔티티 메서드
        boardAdminRepository.save(board); // DB 반영

        // DTO 변환
        BoardAdminDTO boardAdminDTO = modelMapper.map(board, BoardAdminDTO.class);

        // 엔티티 images → DTO fileNames 변환
        List<String> imageUrls = board.getImages()
                .stream()
                .map(img -> "/uploads/" + img.getUploadPath())
                .collect(Collectors.toList());
        boardAdminDTO.setFileNames(imageUrls);

        return boardAdminDTO;
    }


    @Override
    public BoardAdminDTO get(Long bno) {
        return boardAdminRepository.findById(bno)
                .map(entity -> BoardAdminDTO.builder()
                        .bno(entity.getBno())
                        .title(entity.getTitle())
                        .content(entity.getContent())
                        .writer(entity.getWriter())
                        .build()
                )
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다 bno=" + bno));
    }



    // 글 수정(POST)
    @Override
    public void modify(BoardAdminDTO dto) {
        BoardAdmin entity = boardAdminRepository.findById(dto.getBno())
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다 bno=" + dto.getBno()));
        entity.change(dto.getTitle(), dto.getContent());
        boardAdminRepository.save(entity); // JPA는 save로 update
    }



    @Override
    public void remove(Long bno) {
        boardAdminRepository.deleteById(bno);
    }


    @Override
    public PageAdminResponseDTO<BoardAdminDTO> list(PageAdminRequestDTO pageAdminRequestDTO) {
        String[] types = pageAdminRequestDTO.getTypes();
        String keyword = pageAdminRequestDTO.getKeyword();
        Pageable pageable = pageAdminRequestDTO.getPageable("bno");

        // Repository의 searchAll1 메서드가 이미 수정된 정렬 로직을 반영합니다.
        Page<BoardAdmin> result = boardAdminRepository.searchAll1(types, keyword, pageable);

        List<BoardAdminDTO> dtoList = result.getContent().stream()
                .map(boardAdmin -> {
                    BoardAdminDTO dto = modelMapper.map(boardAdmin, BoardAdminDTO.class);
                    dto.setViewCount(boardAdmin.getViewCount());
                    return dto;
                })
                .collect(Collectors.toList());

        PageAdminResponseDTO<BoardAdminDTO> responseDTO = PageAdminResponseDTO.<BoardAdminDTO>withAll()
                .pageAdminRequestDTO(pageAdminRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();

        responseDTO.makePageList();

        return responseDTO;
    }

    @Override
    public BoardAdmin getBoard(Long bno) {
        // 조회수 증가
        boardAdminRepository.increaseViewCount(bno);

        // DB 반영 후 엔티티 다시 조회
        return boardAdminRepository.findById(bno)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));
    }

    @Override
    public List<BoardAdminDTO> getBoardList(Pageable pageable) {
        Page<BoardAdmin> pageResult = boardAdminRepository.findAll(pageable);

        // BoardAdmin -> BoardAdminDTO 변환
        List<BoardAdminDTO> dtoList = pageResult.stream()
                .map(board -> {
                    BoardAdminDTO dto = new BoardAdminDTO();
                    dto.setBno(board.getBno());
                    dto.setTitle(board.getTitle());
                    dto.setWriter(board.getWriter());
                    dto.setRegDate(board.getRegDate());
                    dto.setViewCount(board.getViewCount());
                    // likeCount나 liked는 컨트롤러에서 처리
                    return dto;
                })
                .collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public void save(BoardAdminDTO dto) {
        // dto는 여기서만 유효함
        BoardAdmin entity = dtoToEntity(dto);
        boardAdminRepository.save(entity);
    }

    // dto를 entity로 변환
    private BoardAdmin dtoToEntity(BoardAdminDTO dto) {
        return BoardAdmin.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .notice(dto.isNotice())
                .build();
    }



    }








