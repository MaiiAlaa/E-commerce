package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductResponseDTO;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.ProductImages;
import org.example.e_commerce.Repository.ProductImagesRepository;
import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.Repository.CategoryRepository;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private SignUpResponseDTO responseDTO = new SignUpResponseDTO();

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImagesRepository productImagesRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository , ProductImagesRepository productImagesRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImagesRepository = productImagesRepository;
    }

    public Product convertToEntity(ProductRequestDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public SignUpResponseDTO addProduct(ProductRequestDTO productDTO) {
        if (productDTO.getProductName() == null || productDTO.getPrice() == null ||
                productDTO.getDescription() == null || productDTO.getCategoryID() == null ||
                productDTO.getWarrantyPeriod() == null || productDTO.getManufacturer() == null
                || productDTO.getMainImageUrl() == null )
        {
            responseDTO.setMessage("Fill the data ");
            responseDTO.setStatusCode(-1L);
            return responseDTO;
        }

        Product productExist = productRepository.findByProductName(productDTO.getProductName());
        if (productExist != null) {
            responseDTO.setMessage("Product Already Exists. Update if you want");
            responseDTO.setStatusCode(-2L);
            return responseDTO;
        }

        Category category = categoryRepository.findById(productDTO.getCategoryID()).orElse(null);
        if (category == null) {
            responseDTO.setMessage("Category not found with id: " + productDTO.getCategoryID());
            responseDTO.setStatusCode(-3L);
            return responseDTO;
        }

        Product productNew = convertToEntity(productDTO);
        productNew.setCategory(category);
        productRepository.save(productNew);
        responseDTO.setMessage("Added Successfully");
        responseDTO.setStatusCode(0L);
        return responseDTO;
    }

    public SignUpResponseDTO updateProduct(ProductRequestDTO productDTO) {
        Product productExist = productRepository.findByProductName(productDTO.getProductName());
        if (productExist != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryID()).orElse(null);
            if (category == null) {
                responseDTO.setMessage("Category not found with id: " + productDTO.getCategoryID());
                responseDTO.setStatusCode(-3L);
                return responseDTO;
            }

            productExist.setCategory(category);
            productExist.setPrice(productDTO.getPrice());
            productExist.setDescription(productDTO.getDescription());
            productExist.setWarrantyPeriod(productDTO.getWarrantyPeriod());
            productExist.setManufacturer(productDTO.getManufacturer());

            // Update stock quantity
            productExist.setStockQuantity(productDTO.getStockQuantity());

            productRepository.save(productExist);
            responseDTO.setMessage("Product updated");
            responseDTO.setStatusCode(0L);
            return responseDTO;
        }

        responseDTO.setMessage("Product didn't Exist. Please Add Product");
        responseDTO.setStatusCode(-4L);
        return responseDTO;
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        List<String> imageUrls = productImagesRepository.findAllByProduct_ProductId(product.getProductId())
                .stream()
                .map(ProductImages::getImageUrl)
                .collect(Collectors.toList());

        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getCategory().getCategoryid(),
                product.getCategory().getName(),
                product.getImageUrl(), // main image
                imageUrls // additional images
        );
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchByNameOrManufacturer(searchTerm);
    }
    public List<Product>findProductCategoryId(Long categoryId){
        return productRepository.findProductCategoryId(categoryId);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDtoForAllProducts)
                .collect(Collectors.toList());
    }

    // Method used by getAllProducts to exclude imageUrls
    private ProductResponseDTO convertToDtoForAllProducts(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getCategory().getCategoryid(),
                product.getCategory().getName(),
                product.getImageUrl(), // Main image URL
                null // Exclude additional image URLs
        );
    }


}
