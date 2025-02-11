package com.ecommerce.ecom.service;

import com.ecommerce.ecom.exceptions.APIException;
import com.ecommerce.ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.ecom.mappers.CategoryMapper;
import com.ecommerce.ecom.model.Category;
import com.ecommerce.ecom.payload.CategoryDTO;
import com.ecommerce.ecom.payload.CategoryResponse;
import com.ecommerce.ecom.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sortByAndDir = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndDir);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();

        if(categories.isEmpty()){
            throw new APIException("There is no category in this page");
        }

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(categoryMapper::toCategoryDTO)
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(pageNumber);
        categoryResponse.setPageSize(pageSize);
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if(categoryRepository.findByCategoryName(categoryDTO.getCategoryName()).isPresent()){
            throw new APIException("Category with name " + categoryDTO.getCategoryName() +
                    " already exists");
        }

        Category category = categoryMapper.toCategory(categoryDTO);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryDTO(savedCategory);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);

        if(optionalCategory.isEmpty()){
            throw new ResourceNotFoundException("Category", "Id", categoryId);
        }

        Category category = optionalCategory.get();
        categoryRepository.deleteById(categoryId);

        return categoryMapper.toCategoryDTO(category);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Name", categoryDTO.getCategoryName()));

        Category newCategory = new Category(category.getCategoryId(), categoryDTO.getCategoryName());
        Category savedCategory = categoryRepository.save(newCategory);

        return categoryMapper.toCategoryDTO(savedCategory);
    }
}
