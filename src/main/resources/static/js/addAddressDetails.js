
document.getElementById("addrBtn").addEventListener("click", function () {
    new daum.Postcode({
        oncomplete: function (data) {
            document.getElementById("mpostcode").value = data.zonecode;   // 우편번호
            document.getElementById("maddress").value = data.address;     // 기본 주소
            document.getElementById("mdetailAddress").focus();            // 상세주소 입력으로 포커스 이동
        }
    }).open();
});
