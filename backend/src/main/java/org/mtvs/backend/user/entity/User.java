package org.mtvs.backend.user.entity;

import jakarta.persistence.*;
import org.mtvs.backend.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.mtvs.backend.product.entity.ProductUserLink;

@Getter
@Setter
@Entity 
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles;

    // yunzi
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductUserLink> productUserLinks = new ArrayList<>();

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.roles = "ROLE_USER";
    }
}
