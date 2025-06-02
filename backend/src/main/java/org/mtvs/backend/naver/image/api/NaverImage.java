package org.mtvs.backend.naver.image.api;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NaverImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String imgUrl;

    @Column
    private String productName;

    public NaverImage(String image, String productName) {
        this.imgUrl = image;
        this.productName = productName;
    }

    public void updateImgUrl(String newImgUrl) {
        this.imgUrl = newImgUrl;
    }

    @Override
    public String toString() {
        return "NaverImage{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
