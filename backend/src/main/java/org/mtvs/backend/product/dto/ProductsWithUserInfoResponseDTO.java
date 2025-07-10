package org.mtvs.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.userskin.entity.Userskin;

import java.util.List;

// 전체 응답을 위한 래퍼 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductsWithUserInfoResponseDTO {
    private UserInfoDTO userInfo;
    private List<ProductDTO> products;

    public static ProductsWithUserInfoResponseDTO create(Userskin userskin, List<ProductDTO> products) {
        UserInfoDTO userInfo = UserInfoDTO.fromUserskin(userskin);
        return new ProductsWithUserInfoResponseDTO(userInfo, products);
    }
}
