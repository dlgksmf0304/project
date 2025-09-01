package org.mbc.czo.function.product.service;

// [lombok.extern.java.Log]
// 롬복(Lombok)이 제공하는 로거(Logger) 주입용 어노테이션을 쓰기 위한 import.
// @Log를 붙이면 log.info(), log.warning() 같은 로그 함수들을 바로 쓸 수 있어요.
import lombok.extern.java.Log;

// [org.springframework.stereotype.Service]
// 이 클래스가 스프링의 "서비스 레이어" 컴포넌트임을 표시.
// 스프링이 자동으로 빈(Bean)으로 등록해서 다른 곳에서 주입(@Autowired 등)해 쓸 수 있게 해줘요.
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service // 이 클래스를 스프링 서비스로 등록한다는 표시
@Log     // 롬복이 제공하는 Logger를 자동으로 만들어줘요 (log.info 등 사용 가능)
public class FileService {
    /*
     * 목적:
     *  - 서버로 들어온 파일(바이트 데이터)을
     *  - 파일명이 겹치지 않게(UUID로 새 이름을 만들어)
     *  - 지정한 폴더(uploadPath)에 저장하고
     *  - 실제로 저장된 "파일 이름"만 반환한다.
     */

    // 파일 업로드 메서드
    // uploadPath: 파일을 저장할 폴더 경로 (예: "C:/shop/item")
    // originalFileName: 원본 파일명 (예: "cat.png")
    // fileData: 파일 내용(바이트 배열) — 진짜 파일의 속살!
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        // 1) 업로드 폴더(우편함) 준비하기
        File uploadDir = new File(uploadPath);  // 해당 경로의 폴더를 가리키는 객체
        if (!uploadDir.exists()) {              // 폴더가 없으면
            boolean created = uploadDir.mkdirs(); // 필요한 상위폴더까지 전부 생성(예: C:/shop/image)
            if (created) {
                log.info("업로드 디렉토리를 생성했습니다: " + uploadPath);
            } else {
                log.warning("업로드 디렉토리 생성에 실패했습니다: " + uploadPath);
                // 폴더가 없는데 만들지도 못했다면 더 진행 불가 → 예외 던짐
                throw new Exception("디렉토리 생성에 실패했습니다: " + uploadPath);
            }
        }

        // 2) 파일명이 겹치지 않도록 랜덤 이름(UUID) 만들기
        UUID uuid = UUID.randomUUID(); // 예: "f6b1a2c3-...-d9e0"

        // 3) 원본 파일의 확장자만 뽑아오기 (".png" 같은 부분)
        //    - lastIndexOf(".")가 -1이면 확장자가 없는 파일이므로 substring에서 에러가 날 수 있음.
        //    - 지금 코드는 확장자가 반드시 있다고 가정하고 있어요.
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 예: ".png"

        // 4) 최종 저장 파일명 만들기: "UUID + 확장자"
        //    - 예: "f6b1a2c3-...-d9e0.png"
        String savedFileName = uuid.toString() + extension;

        // 5) 파일이 실제로 저장될 "전체 경로" 만들기
        //    - 예: "C:/shop/image/f6b1a2c3-...-d9e0.png"
        //    - 여기서는 "/"를 썼지만, 운영체제에 따라 구분자가 다를 수 있어요(Windows는 보통 "\").
        //      자바는 대부분 "/"도 이해하지만, 더 안전하게는 Paths.get(uploadPath, savedFileName) 사용을 권장.
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        // 6) 파일을 디스크에 쓰기 위한 파이프(출구) 열기
        //    FileOutputStream: 바이트 단위로 파일로 내보내는 통로.
        //    같은 이름의 파일이 이미 있으면 덮어씁니다.
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);

        // 7) 진짜 파일 데이터 쓰기
        fos.write(fileData); // 바이트 배열을 통째로 기록

        // 8) 통로 닫기 (안 닫으면 자원 누수 가능)
        fos.close();

        // 9) 컨트롤러/서비스 쪽에서 DB에 이 이름을 저장해두고, 나중에 이 이름으로 파일을 조회/표시해요.
        return savedFileName; // 예: "f6b1a2c3-...-d9e0.png"
    }

    // 파일 삭제 메서드
    // filePath: 지우고 싶은 파일의 "전체 경로" (예: "C:/shop/image/f6b1a2c3-...-d9e0.png")
    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath); // 해당 경로의 파일을 가리키는 객체

        if (deleteFile.exists()) { // 파일이 실제로 있으면
            deleteFile.delete();   // 삭제 시도 (성공/실패 boolean 반환이지만 여기서는 체크 생략)
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
