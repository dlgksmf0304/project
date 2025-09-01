package org.mbc.czo.function.member.email.emailData;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원가입 / 메일 전송 관련 응답 코드 Enum
 */
@Getter
@RequiredArgsConstructor
public enum UserSignUpResponseCode implements DataResponseCode {
    SUCCESS(200, "메일 전송 성공"),
    MAIL_SEND_FAILED(500, "메일 전송 실패");

    private final int code;
    private final String message;
}
