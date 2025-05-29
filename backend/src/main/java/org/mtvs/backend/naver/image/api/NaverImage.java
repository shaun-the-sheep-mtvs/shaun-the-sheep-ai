package org.mtvs.backend.naver.image.api;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
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

    @Override
    public String toString() {
        return "NaverImage{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
