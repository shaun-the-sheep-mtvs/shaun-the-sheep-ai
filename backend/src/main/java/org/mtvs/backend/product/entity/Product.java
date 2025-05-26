package org.mtvs.backend.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.mtvs.backend.user.entity.User;

import java.util.List;

@Table(name = "products")
@Entity
// 매개변수가 없는 기본 생성자 생성 ex. public Product() {}
@NoArgsConstructor
// 클래스 내 모든 필드를 매개변수로 받는 생성자를 생성
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

//    public enum FormulationType {
//        건성, 지성, 복합성, 민감성, 수부지
//    }

    @Column(name = "formulation")
    private String formulationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ingredients", columnDefinition = "jsonb")
    private List<String> ingredients;

    @Column(name = "recommended_type")
    private String recommendedType;

    @Column(name = "product_name")
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormulationType() {
        return formulationType;
    }

    public void setFormulationType(String formulationType) {
        this.formulationType = formulationType;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getRecommendedType() {
        return recommendedType;
    }

    public void setRecommendedType(String recommendedType) {
        this.recommendedType = recommendedType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}