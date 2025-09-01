package org.mbc.czo.function.product.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.product.domain.Item;
import org.mbc.czo.function.product.domain.ItemImg;
import org.mbc.czo.function.product.dto.ItemFormDto;
import org.mbc.czo.function.product.dto.ItemImgDto;
import org.mbc.czo.function.product.dto.ItemSearchDto;
import org.mbc.czo.function.product.dto.MainItemDto;
import org.mbc.czo.function.product.repository.ItemImgRepository;
import org.mbc.czo.function.product.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        // 상품 등록
        Item item = itemFormDto.createItem(); // 아이템 생성하여
        itemRepository.save(item); // 저장

        // 이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i==0)
                itemImg.setRepimgYn("Y"); // 대표 이미지 여부 값
                // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로 설정. 나머지 상품 이미지는 "N"로 설정
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        return item.getId();
    }


    // 상품 수정
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        // 상품 등록 화면에서 전달 받은 상품 아이디를 이용해 상품엔티티 조회
        item.updateItem(itemFormDto); // 상품 엔티티 업데이트
        List<Long> itemImgIds = itemFormDto.getItemImgIds(); // 상품 이미지 아이디 리스트를 조회
        // 이미지 등록
        for(int i=0; i<itemImgIds.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
            // 상품 이미지를 업데이트 하기 위해 상품 이미지 아이디와 상품 이미지 파일 정보를 전달
        }
        return item.getId();
    }

    // 등록된 상품을 불러오는 메소드 (상세페이지)
    @Transactional(readOnly = true) // 데이터 일관성 보장, 불필요한 update sql 생성 방지 = 트랜잭션 안에서 조회하면 같은 트랜잭션 내에선 일관된 데이터를 볼 수 있음
    public ItemFormDto getItemDtl(Long itemId){
        itemRepository.updateView(itemId); // 조회수 증가 로직 추가
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId); // 해당 상품 이미지를 조회
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        log.info("getItemDtl+ getItemDtl");
        for(ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg); // itemImg를 ItemImgDto로 변환
            itemImgDtoList.add(itemImgDto);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new); // 상품의 아이디를 통해 상품 엔티티 조회
        ItemFormDto itemFormDto = ItemFormDto.of(item); // item엔티티를 dto로 변환
        itemFormDto.setItemImgDtoList(itemImgDtoList); // dto에 이미지 파일 추가
        log.info("getItemDtl result for itemId {}: {}", itemId, itemFormDto);
        log.info("Item images: {}", itemImgDtoList);
        return itemFormDto;
    }

    // ▼ [추가] 좋아요를 증가시키는 서비스 메소드
    public void addLike(Long itemId) {
        itemRepository.addLike(itemId);
    }


    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // 상품 조회 (관리자용)
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // 메인페이지 상품 조회 (사용자)
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }


    @Transactional
    public void deleteItem(List<Long> itemIds) {
        for (Long itemId : itemIds) {
            // 1. 상품 이미지 먼저 삭제
            itemImgRepository.deleteByItemId(itemId);

            // 2. 다른 연관 데이터도 삭제 (필요한 경우)
            // cartItemRepository.deleteByItemId(itemId);

            // 3. 마지막에 상품 삭제
            itemRepository.deleteById(itemId);
        }
    }


}
