package org.mtvs.backend.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductWithImageDTO {
	private Integer id;
	private Byte formulationId;
	private List<String> ingredients;
	private String recommendedType;
	private String productName;
	private String userId;
	private String imageUrl;
}
