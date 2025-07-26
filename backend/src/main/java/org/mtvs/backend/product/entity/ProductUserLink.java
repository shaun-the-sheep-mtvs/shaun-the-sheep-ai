package org.mtvs.backend.product.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtvs.backend.user.entity.User;

@Data
@Table(name = "product_user_link")
@Entity
@IdClass(ProductUserLinkId.class)
@NoArgsConstructor
@AllArgsConstructor
public class ProductUserLink {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

}
