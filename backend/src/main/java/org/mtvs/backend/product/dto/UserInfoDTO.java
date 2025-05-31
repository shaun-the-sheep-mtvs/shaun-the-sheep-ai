package org.mtvs.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.user.entity.User;

import java.util.List;

// 사용자 정보 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInfoDTO {
    private String skinType;
    private List<String> troubles;

    public static UserInfoDTO fromUser(User user) {
        return new UserInfoDTO(
                user.getSkinType() != null ? user.getSkinType().name() : null,
                user.getTroubles()
        );
    }

}
