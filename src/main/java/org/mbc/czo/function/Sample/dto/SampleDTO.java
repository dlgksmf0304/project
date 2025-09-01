package org.mbc.czo.function.Sample.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SampleDTO{
    // 이너 클래스로 클래스 안쪽에 클래스를 선언할 때 활용 된다.
    // 필드
    private String p1, p2, p3;

} // 이너 클래스 종료