package com.ecommerce.ecom.mappers;

import com.ecommerce.ecom.model.Category;
import com.ecommerce.ecom.payload.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toCategoryDTO(Category category);

    Category toCategory(CategoryDTO categoryDTO);

}
