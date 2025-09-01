package org.mbc.czo.function.image.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass
public abstract class BaseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String storedFileName;
    private String uploadPath;

    private LocalDateTime uploadDate = LocalDateTime.now();

    // 생성자, Getter/Setter
    public BaseImage() {}

    public BaseImage(String originalFileName, String storedFileName, String uploadPath) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.uploadPath = uploadPath;
    }

    // Getter / Setter 생략
}
