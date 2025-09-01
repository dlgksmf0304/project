document.getElementById("registerForm").addEventListener("submit", function(event) {
    const password = document.querySelector("input[name='mpassword']").value;
    const passwordCheck = document.querySelector("input[name='mpasswordcheck']").value;

    if (password !== passwordCheck) {
        event.preventDefault(); // 폼 제출 막기
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        return false;
    }
});

document.getElementById("registerForm").addEventListener("submit", function(event) {
    const pw = document.getElementById("mpassword").value;
    const pwCheck = document.getElementById("mpasswordcheck").value;
    const pwMessage = document.getElementById("pwMessage");

    // 정규식: 최소 6자리, 영문자+숫자 필수
    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/;

    if (!pwRegex.test(pw)) {
        event.preventDefault();
        pwMessage.textContent = "비밀번호는 최소 6자리 이상, 영문자와 숫자를 포함해야 합니다.";
        return;
    }

    if (pw !== pwCheck) {
        event.preventDefault();
        pwMessage.textContent = "비밀번호 확인이 일치하지 않습니다.";
        return;
    }

    pwMessage.textContent = "";
});