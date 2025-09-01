package org.mbc.czo.function.product.service;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.mbc.czo.function.product.domain.ItemImg;
import org.mbc.czo.function.product.repository.ItemImgRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}") // application.properties 파일에 등록한 itemImgLocation 값을 불러와서 itemImgLocation에 넣음
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    // 상품 이미지 등록
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){ // 이미지 파일이 있으면
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); // UUID+확장자가 리턴됨
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
        // imgName: 실제 로컬에 저장된 상품 이미지 파일의 이름
        // oriImgName: 업로드했던 상품 이미지 파일의 원래 이름
        // imgUrl: 업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
    }

    // 상품 이미지 수정
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
         if(!itemImgFile.isEmpty()){ // 이미지가 존재하면! 확인대상이 업로드된 파일
             ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);
        // itemImgRepository.findById()는 JPA Repository의 메서드입니다.
        // 이 메서드는 DB에서 엔티티를 조회하면서, 조회된 엔티티를 영속 상태로 관리. 즉, savedItemImg는 현재 영속 상태

         // 기존 이미지 파일 삭제
         if (!StringUtils.isEmpty(savedItemImg.getImgName())) { // 기존에 등록된 상품 이미지 파일이 있을 경우 해당 파일 삭제 : 확인대상이 DB에 저장된 이미지 이름(String)
            fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());

         }

         String oriImgName = itemImgFile.getOriginalFilename();
         String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); // 업데이트한 상품 이미지 파일을 업로드
         String imgUrl = "/images/item/" + imgName; // 실제 파일은 src/main/resources/static/images/item/에 있어야 브라우저가 볼 수 있음
         savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
         // 변경된 상품 이미지 정보를 세팅
         // 중요! 상품 등록 때처럼 itemImgRepository.save()로직을 호출하지 않음. savedItemImg 엔티티는 현재 영속 상태이므로 데이터 변경만으로
         // 변경 감지 기능이 동작해 트랜잭션이 끝날 때 update 쿼리가 실행됨

         }
    }

}
