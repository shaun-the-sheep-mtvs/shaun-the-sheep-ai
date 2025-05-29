package org.mtvs.backend.naver.image.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private String imageUrl;
    private String productName;
}
