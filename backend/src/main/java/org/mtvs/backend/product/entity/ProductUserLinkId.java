package org.mtvs.backend.product.entity;

import java.io.Serializable;
import java.util.Objects;

import java.io.Serializable;
import java.util.Objects;

public class ProductUserLinkId implements Serializable {
    private Integer productId;
    private String userId;

    public ProductUserLinkId() {}

    public ProductUserLinkId(Integer productId, String userId) {
        this.productId = productId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductUserLinkId that = (ProductUserLinkId) o;
        return Objects.equals(productId, that.productId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, userId);
    }
}