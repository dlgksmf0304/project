package org.mbc.czo.function.boardAdmin.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardAdminImageDTO {

    private String id;

    private String fileName;

    private int ord; // 이미지 순서
}
