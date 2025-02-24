package com.ecommerce.ecom.service;

import com.ecommerce.ecom.payload.ProductDTO;
import com.ecommerce.ecom.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getProducts();

    ProductResponse getByCategory(Long categoryId);

    ProductResponse getByKeyword(String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
