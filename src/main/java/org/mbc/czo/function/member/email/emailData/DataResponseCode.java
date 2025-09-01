package org.mbc.czo.function.member.email.emailData;

public interface DataResponseCode {
    int getCode();      // HTTP 상태 코드 또는 비즈니스 코드
    String getMessage(); // 응답 메시지
}
