package org.mtvs.backend.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.product.entity.Product;
import org.mtvs.backend.user.entity.User;

import java.util.List;



@NoArgsConstructor
@Getter
@Setter
public class ProductWithImageDTO {
    private String id;
    private String formulation;
    private List<String> ingredients;
    private String recommendedType;
    private String productName;
    private String userId;
    private String imageUrl;
}
