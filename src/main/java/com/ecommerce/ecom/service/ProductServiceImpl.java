package com.ecommerce.ecom.service;

import com.ecommerce.ecom.exceptions.APIException;
import com.ecommerce.ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.ecom.mappers.ProductMapper;
import com.ecommerce.ecom.model.Category;
import com.ecommerce.ecom.model.Product;
import com.ecommerce.ecom.payload.ProductDTO;
import com.ecommerce.ecom.payload.ProductResponse;
import com.ecommerce.ecom.repositories.CategoryRepository;
import com.ecommerce.ecom.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    public ProductServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository, ProductMapper productMapper, FileService fileService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.fileService = fileService;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "Id", categoryId));

        List<Product> categoryProducts = category.getProducts();

        for(Product product1 : categoryProducts){
            if(product1.getName().equals(productDTO.getName())){
                throw new APIException("Product with name: " + productDTO.getName() + " already exists.");
            }
        }

        Product product = productMapper.toProduct(productDTO);

        product.setImage("default.png");
        product.setCategory(category);
        Double specialPrice = product.getPrice() - (product.getDiscount() * 0.01 * product.getPrice());
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDTO(savedProduct);
    }

    @Override
    public ProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sortDetails = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortDetails);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        if(productPage.isEmpty()){
            throw new APIException("There is no product in this page.");
        }

        List<ProductDTO> productDTOS = productPage.map(productMapper::toProductDTO).toList();
        ProductResponse response = new ProductResponse();

        response.setContent(productDTOS);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setLastPage(productPage.isLast());

        return response;
    }

    @Override
    public ProductResponse getByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortDir, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Id", categoryId));

        Sort sortDetails = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortDetails);
        Page<Product> productPage = productRepository.findByCategory(category, pageDetails);

        if(productPage.isEmpty()){
            throw new APIException("There is no product with the specified category in this page.");
        }

        List<ProductDTO> productDTOS = productPage.map(productMapper::toProductDTO).toList();

        ProductResponse response = new ProductResponse();

        response.setContent(productDTOS);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setLastPage(productPage.isLast());

        return response;
    }

    @Override
    public ProductResponse getByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortDir, String keyword) {
        Sort sortDetails = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortDetails);
        Page<Product> productPage = productRepository.findByNameLikeIgnoreCase("%" + keyword + "%", pageDetails);

        if(productPage.isEmpty()){
            throw new APIException("There is no product with the specified keyword in this page.");
        }

        List<ProductDTO> productDTOS = productPage.map(productMapper::toProductDTO).toList();

        ProductResponse response = new ProductResponse();

        response.setContent(productDTOS);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setLastPage(productPage.isLast());

        return response;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "Id", productId));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        product.setQuantity(productDTO.getQuantity());
        product.setImage(productDTO.getImage() == null ? product.getImage() : productDTO.getImage());

        product.setSpecialPrice(productDTO.getPrice() - (productDTO.getDiscount() * 0.01 * productDTO.getPrice()));

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductDTO(savedProduct);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "Id", productId));

        productRepository.delete(product);

        return productMapper.toProductDTO(product);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "Id", productId));

        String fileName = fileService.uploadImage(path, image);

        product.setImage(fileName);
        Product savedProduct = productRepository.save(product);

        return productMapper.toProductDTO(savedProduct);
    }
}
