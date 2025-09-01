package org.mbc.czo.function.member.email.emailData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * API 응답 데이터를 감싸는 Wrapper 클래스
 */
@Getter
@Setter
@AllArgsConstructor
public class DataResponse<T extends DataResponseCode> {
    private int code;
    private String message;

    public DataResponse(T responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}