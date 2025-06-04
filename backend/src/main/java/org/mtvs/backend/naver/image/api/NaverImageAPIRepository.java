package org.mtvs.backend.naver.image.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NaverImageAPIRepository extends JpaRepository<NaverImage, Long> {

    boolean findByProductName(String productName);


    @Query("SELECT n.imgUrl FROM NaverImage n WHERE n.productName = :productName")
    String findImgUrlByProductName(@Param("productName") String productName);

    boolean existsNaverImageByProductName(String productName);

    List<NaverImage> findNaverImagesByImgUrlIs(String imgUrl);
//    boolean existsNaverImageByProductName(String productName);
}
