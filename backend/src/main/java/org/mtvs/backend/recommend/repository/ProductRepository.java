package org.mtvs.backend.recommend.repository;

import org.mtvs.backend.recommend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
