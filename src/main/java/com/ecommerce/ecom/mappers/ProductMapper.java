package com.ecommerce.ecom.mappers;

import com.ecommerce.ecom.model.Product;
import com.ecommerce.ecom.payload.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);

    Product toProduct(ProductDTO productDTO);

}
