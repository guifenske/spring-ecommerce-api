package com.ecommerce.ecom.repositories;

import com.ecommerce.ecom.model.Category;
import com.ecommerce.ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByNameLikeIgnoreCase(String keyword, Pageable pageable);
}
