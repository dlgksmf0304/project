package org.mbc.czo.function.boarduser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.boarduser.domain.Reply;
import org.mbc.czo.function.boarduser.dto.PageRequestDTO;
import org.mbc.czo.function.boarduser.dto.PageResponseDTO;
import org.mbc.czo.function.boarduser.dto.ReplyDTO;
import org.mbc.czo.function.boarduser.repository.ReplyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;

     @Override
     public Long register(ReplyDTO replyDTO) {
         // 댓글 등록
         log.info("모델로 변환전 객체 : " + replyDTO);

         Reply reply = modelMapper.map(replyDTO, Reply.class);

         log.info("모델로 변환된 객체 : " + reply);

         // dto를 엔티티로 변환
         Long rno = replyRepository.save(reply).getRno();
         //                         저정     후  번호를 가져와 rno에 넣음
         return rno;
     }

    @Override
    public ReplyDTO read(Long rno) {
        // 댓글 번호가 들어오면 자세히 보기용
        Optional<Reply> replyOptional = replyRepository.findById(rno);
        //                                  select * from reply where rno = rno

        Reply reply = replyOptional.orElseThrow(); // 객체가 있으면

        return modelMapper.map(reply, ReplyDTO.class);
        //                     엔티티가 dto로 변환되어 리턴
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(

                pageRequestDTO.getPage() <=0 ? 0 : pageRequestDTO.getPage() -1,

                pageRequestDTO.getSize(),
                Sort.by("rno").ascending() // 댓글은 처음 등록한 것이 위로 올라옴!
        );

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        List<ReplyDTO> dtoList = result.getContent().stream()
                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public void modify(ReplyDTO replyDTO) {

        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());


        Reply reply = replyOptional.orElseThrow();

        reply.changeText(replyDTO.getReplytext()); // 댓글에 내용만 가져와


        replyRepository.save(reply); // 있으면 update



    }

    @Override
    public void remove(Long rno) {

        replyRepository.deleteById(rno);  // 댓글 번호를 이용해서 삭제

    }
}
