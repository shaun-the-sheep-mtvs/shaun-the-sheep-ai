package org.mtvs.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.user.entity.User;
import org.mtvs.backend.userskin.entity.Userskin;

import java.util.List;

// 사용자 정보 DTO
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInfoDTO {
    private String skinType;
    private List<String> troubles;

    public static UserInfoDTO fromUserskin(Userskin userskin) {
        if (userskin == null) {
            return new UserInfoDTO(null, List.of());
        }
        
        String skinType = userskin.getSkinType() != null ? 
            userskin.getSkinType().getSkinType().getKoreanName() : null;
            
        List<String> concerns = userskin.getConcerns() != null ?
            userskin.getConcerns().stream()
                .map(concern -> concern.getDescription())
                .toList() : List.of();
                
        return new UserInfoDTO(skinType, concerns);
    }

}
