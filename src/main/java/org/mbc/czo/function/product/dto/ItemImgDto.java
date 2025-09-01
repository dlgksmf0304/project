package org.mbc.czo.function.product.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mbc.czo.function.product.domain.ItemImg;
import org.modelmapper.ModelMapper;

@Getter @Setter @ToString
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg,ItemImgDto.class);
    }

}
