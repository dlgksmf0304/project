
console.log("JS 파일 실행 시작");

import axios from 'https://cdn.jsdelivr.net/npm/axios@1.6.6/dist/esm/axios.min.js';

//js는 싱글스레드 -> 하나의 작업 끝날때 까지 대기
//이미지 업로드 중 동작이 멈춘것처럼 보일 수 있음
//async 함수는 await 사용가능 await로 처리하면 UI가 멈추지 않고 사용자 경험이 부드러움
// 공용 업로드 함수
async function uploadImage(url, files, extraData = {}) {
    //multipart(files)/form-data(id)
    //files: [파일1, 파일2], userId: 1
    const formData = new FormData();

    //files라는 이름의 file 배열
    //서버에서 Spring의 @RequestParam("files") List<MultipartFile>로 받음
    for (let file of files) formData.append("files", file);

    //추가 데이터값 배열로 저장
    for (let key in extraData) formData.append(key, extraData[key]);


   /* // CSRF 토큰 가져오기 -> 토큰 키면 필요
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const response = await axios.post(url, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
            [header]: token   // 토큰을 헤더에 추가
        }
    });*/
    console.log("uploadImages.js 로드됨");

    // axios.post → POST 방식으로 서버에 요청
    // url → 서버 엔드포인트
    // formData → 전송할 데이터
    // headers → multipart/form-data 설정 필수 (파일 업로드용)
    // await 때문에 서버 응답이 올 때까지 기다림
    // response 안에는 서버에서 보낸 데이터가 들어있음 (response.data)
    const response = await axios.post(url, formData, {
        headers: { "Content-Type": "multipart/form-data" }
    });
    return response;
}

// 버튼 클릭 이벤트 바인딩
document.querySelectorAll(".uploadBtn").forEach(button => {
    button.addEventListener("click", async () => {
        console.log("addEventListener.js 로드됨");
        const type = button.dataset.type;
        let input;
        let extraData = {};

        switch(type) {
            case "profile":
                input = document.getElementById("profileInput");
                extraData.userId = button.dataset.userId;
                break;
            case "product":
                input = document.getElementById("productInput");
                extraData.productId = button.dataset.productId;
                break;
            case "board":
                input = document.getElementById("boardInput");
                extraData.boardId = button.dataset.boardId;
                break;
        }

        const files = input.files;
        if (!files.length) { alert("파일을 선택해주세요."); return; }

        try {
            //분기별로 service나눌거면 필요
            //const res = await uploadImage(`/api/upload/${type}`, files, extraData);
            const res = await uploadImage(`/api/upload/image`, files, extraData);
            console.log(res.data);
            alert(`${type} 업로드 성공!`);
        } catch (err) {
            console.error(err);
            alert(`${type} 업로드 실패!`);
        }
    });
});
/*
document.addEventListener("DOMContentLoaded", () => {

});*/
