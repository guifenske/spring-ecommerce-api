package com.ecommerce.ecom.service;

import com.ecommerce.ecom.payload.CategoryDTO;
import com.ecommerce.ecom.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    CategoryDTO createCategory(CategoryDTO category);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
