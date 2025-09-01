document.getElementById("sendEmailBtn").addEventListener("click", function () {
    const email = document.getElementById("memail").value;
    if (!email) {
        alert("이메일을 입력해주세요.");
        return;
    }

    fetch(`/api/mail/send?receiver=${encodeURIComponent(email)}`)
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {  // DataResponse의 success 값 확인
                document.getElementById("emailMessage").textContent =
                    `인증번호가 ${email}로 발송되었습니다.`;
                document.getElementById("verifySection").style.display = "block";
                document.getElementById("emailMessage").style.color = "green";
                // 필요한 경우 emailSent 상태를 JS로 바꿔서 다음 폼 보여주기
            } else {
                document.getElementById("emailMessage").textContent = `${data.message}`;
                document.getElementById("emailMessage").style.color = "red";
            }
        })
        .catch(err => {
            console.error(err);
            document.getElementById("emailMessage").textContent = "메일 발송 중 오류 발생";
            document.getElementById("emailMessage").style.color = "red";
        });
});

document.getElementById("verifyBtn").addEventListener("click", function () {
    const email = document.getElementById("memail").value;
    const code = document.getElementById("emailCode").value;

    if (!code) {
        alert("인증번호를 입력해주세요.");
        return;
    }

    fetch('/api/mail/verify', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `receiver=${encodeURIComponent(email)}&code=${encodeURIComponent(code)}`
    })

        .then(response => response.json())
        .then(data => {
            if (data.verified) {
                alert("이메일 인증 완료!");
                // 인증 완료 후 회원가입 폼 활성화
                document.getElementById("hiddenMemail").value = email;
                document.getElementById("registerForm").style.display = "block";
            } else {
                alert("인증번호가 맞지 않습니다.");
            }
        })
        .catch(err => {
            console.error(err);
            alert("인증 중 오류 발생");
        });
});