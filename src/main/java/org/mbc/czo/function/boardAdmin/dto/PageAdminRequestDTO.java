package org.mbc.czo.function.boardAdmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageAdminRequestDTO {

    private List<BoardAdminDTO> dtoList;

    @Builder.Default
    private int page = 1;  // 첫 페이지

    @Builder.Default
    private int size = 10; // 게시물 수


    private String type;  // 다중 검색 (검색 종류)
    public String[] getTypes(){
        if(type == null || type.isEmpty()){
            return null;
        }
        return type.split("");
    }


    public Pageable getPageable(String...props){
        return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
    }

    private String keyword;  // 폼박스 내용

    private String link;  // 페이징 번호 처리시 문자열
    public String getLink(){
        if(link == null){
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(type != null && type.length() > 0){
                builder.append("&type=" + type);
            }
            if(keyword != null){
                try{
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e){
                }
            }
            link = builder.toString();
        }
    return link;
    }




}
