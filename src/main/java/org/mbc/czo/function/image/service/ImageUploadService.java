package org.mbc.czo.function.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.repository.BoardAdminRepository;
import org.mbc.czo.function.image.domain.BoardAdminImages;
import org.mbc.czo.function.image.domain.MemberProfileImage;
import org.mbc.czo.function.image.repository.BoardAdminImageJpaRepository;
import org.mbc.czo.function.image.repository.MemberProfileImageJpaRepository;
import org.mbc.czo.function.member.domain.Member;
import org.mbc.czo.function.member.repository.MemberJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final MemberProfileImageJpaRepository memberProfileImageJpaRepository;
    private final BoardAdminImageJpaRepository boardAdminImageJpaRepository;

    private final MemberJpaRepository memberJpaRepository;
    private final BoardAdminRepository boardAdminRepository;

/*
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    private final BoardImageRepository boardImageRepository;
    private final BoardRepository boardRepository;
*/

    private final String uploadRoot = "C:/image";

    public String getUUID(){
        return UUID.randomUUID().toString();
    }

    //Transactional = DB 작업이 모두 완료되어야 커밋. 중간에 실패하면 롤백.
    //@Transactional가 붙어있으면 JPA에서 변경 감지후 자동으로 DB 업데이트 (new객체가 아니면)
    @Transactional
    public UploadResult uploadAndSaveImages(List<MultipartFile> files, Map<String, String> extraData) throws IOException {
        List<String> savedUrls = new ArrayList<>();
        String tempKey = null;

        for (MultipartFile file : files) {

            //파일 비어있으면 무시
            if (file.isEmpty()) continue;

            String originalName = file.getOriginalFilename(); // 원본 파일명
            //같은 파일명 충돌 방지

            String extension = originalName.substring(originalName.lastIndexOf(".")); // 확장자
            String uuid = UUID.randomUUID().toString();          //파일명 중복을위한 uuid
            String savedName = uuid + extension;             // 서버에 저장할 이름

            // 타입별 폴더 결정
            String subDir = "etc"; // 기본값
            if (extraData.containsKey("userId")) {
                subDir = "profile";
            } else if (extraData.containsKey("productId")) {
                subDir = "product";
            } else if (extraData.containsKey("boardId")) {
                subDir = "board";
            }

            // 최종 경로
            File folder = new File(uploadRoot, subDir);

            //해당 경로가 존재하는지 확인. 없으면 생성
            if (!folder.exists()) folder.mkdirs();

            File destination = new File(folder, savedName);
            file.transferTo(destination);

            // DB에는 "profile/uuid.png" 만 저장
            String relativePath = subDir + "/" + savedName;



            // DB 저장
            if (extraData.containsKey("userId")) {


                String userId = (extraData.get("userId"));

                //orElseThrow(...) → Optional 안에 값이 있으면 꺼내고, 없으면 예외 발생
                Member member = memberJpaRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

                Optional<MemberProfileImage> resultIMG = memberProfileImageJpaRepository.findByMember_Mid(userId);

                MemberProfileImage profileImage;
                //한 계정에 이미지는 한개, 이미 있으면 update 없으면 new생성후 insert
                if (resultIMG.isPresent()) {
                    MemberProfileImage oldImage = resultIMG.get();
                    Path oldFilePath = Paths.get(uploadRoot, oldImage.getUploadPath()); // uploadRoot + profile/uuid.png
                    File oldFile = oldFilePath.toFile();

                    profileImage = resultIMG.get();
                    profileImage.setOriginalFileName(originalName);
                    profileImage.setStoredFileName(savedName);
                    profileImage.setUploadPath(relativePath);

                    if (oldFile.exists()) {
                        boolean deleted = oldFile.delete();
                        log.info("기존 프로필 파일 삭제 {}: {}", deleted ? "성공" : "실패", oldFile.getAbsolutePath());
                    } else {
                        log.warn("기존 파일이 존재하지 않음: {}", oldFile.getAbsolutePath());
                    }
                } else {
                    profileImage = new MemberProfileImage(originalName, savedName, relativePath, member);
                }

                //MemberProfileImage profileImage = new MemberProfileImage(originalName, savedName, relativePath, member);
                memberProfileImageJpaRepository.save(profileImage);

                // 연관관계 주입 필수
                member.setProfileImage(profileImage);
                memberJpaRepository.save(member);

            } else if (extraData.containsKey("productId")) {
                //Long productId = Long.parseLong(extraData.get("productId"));
                // log.info("productId sucess: {}", productId);
               /* Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("상품 없음"));
                ProductImage productImage = new ProductImage(originalName, savedName, url, product);
                productImageRepository.save(productImage);
                product.getProductImages().add(productImage);*/
            } else if (extraData.containsKey("boardId")) {

                String boardIdStr = extraData.get("boardId");
                // 게시글 생성 전 임시 적으로 boardId값에 temp를 삽입
                if ("temp".equals(boardIdStr)) {

                    tempKey = extraData.get("tempKey");
                    // tempKey 없으면 업로드 중단
                    if (tempKey == null || tempKey.isEmpty()) {
                        throw new IllegalArgumentException("tempKey가 존재하지 않습니다. 업로드를 중단합니다.");
                    }
                    //board admin은 null( 아직 게시물 생성 전이라) 대신 tempKey삽입
                    BoardAdminImages boardAdminImages =  new BoardAdminImages(originalName, savedName, relativePath, tempKey, null);

                    boardAdminImageJpaRepository.save(boardAdminImages);
                }
                // 게시글이 이미 존재하면 boardId로 바로 연결
                else{

                    Long boardId = Long.parseLong(boardIdStr);
                    BoardAdmin boardAdmin = boardAdminRepository.findById(boardId)
                            .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

                    BoardAdminImages boardAdminImages = new BoardAdminImages(
                            originalName, savedName, relativePath, null, boardAdmin
                    );
                    boardAdminImageJpaRepository.save(boardAdminImages);


                }
                // 프론트에는 "/uploads/profile/uuid.png" 로 응답
                String url = "/uploads/" + relativePath;
                savedUrls.add(url);
            }
        }
        return new UploadResult(savedUrls, tempKey);
    }

    @Transactional
    public void deleteImage(String storedFileName) throws IOException {

        BoardAdminImages image = boardAdminImageJpaRepository.findByStoredFileName(storedFileName)
                .orElseThrow(() -> new IllegalArgumentException("이미지 없음"));

        // 실제 파일 삭제
        Path filePath = Paths.get(uploadRoot, image.getUploadPath());
        Files.deleteIfExists(filePath);

        // DB 삭제
        boardAdminImageJpaRepository.delete(image);
    }

    }

