package com.ecommerce.ecom.repositories;

import com.ecommerce.ecom.model.Category;
import com.ecommerce.ecom.model.Product;
import com.ecommerce.ecom.payload.ProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);

    List<Product> findByNameLikeIgnoreCase(String keyword);
}
