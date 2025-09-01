package org.mbc.czo.function.image.service;

import lombok.Getter;

import java.util.List;

@Getter
public class UploadResult {

    private List<String> urls;
    private String tempKey;

    // 생성자, getter
    public UploadResult(List<String> urls, String tempKey) {
        this.urls = urls;
        this.tempKey = tempKey;
    }

}
