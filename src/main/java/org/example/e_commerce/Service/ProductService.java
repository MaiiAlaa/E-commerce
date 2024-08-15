package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductResponseDTO;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Entity.ProductImage;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.Repository.ProductImageRepository;
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

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
    }

    public Product convertToEntity(ProductRequestDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public ProductResponseDTO addProduct(ProductRequestDTO productDTO) {
        if (productDTO.getProductName() == null || productDTO.getPrice() == null ||
                productDTO.getDescription() == null || productDTO.getCategoryID() == null ||
                productDTO.getWarrantyPeriod() == null || productDTO.getManufacturer() == null) {
            throw new RuntimeException("All fields are required.");
        }

        Product productExist = productRepository.findByProductName(productDTO.getProductName());
        if (productExist != null) {
            throw new RuntimeException("Product Already Exists. Update if you want");
        }

        Category category = categoryRepository.findById(productDTO.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDTO.getCategoryID()));

        Product productNew = convertToEntity(productDTO);
        productNew.setCategory(category);
        Product savedProduct = productRepository.save(productNew);

        return convertToDto(savedProduct);
    }

    public ProductResponseDTO updateProduct(ProductRequestDTO productDTO) {
        Product productExist = productRepository.findByProductName(productDTO.getProductName());
        if (productExist == null) {
            throw new RuntimeException("Product didn't Exist. Please Add Product");
        }

        Category category = categoryRepository.findById(productDTO.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDTO.getCategoryID()));

        productExist.setCategory(category);
        productExist.setPrice(productDTO.getPrice());
        productExist.setDescription(productDTO.getDescription());
        productExist.setWarrantyPeriod(productDTO.getWarrantyPeriod());
        productExist.setManufacturer(productDTO.getManufacturer());
        productExist.setStockQuantity(productDTO.getStockQuantity());

        Product updatedProduct = productRepository.save(productExist);
        return convertToDto(updatedProduct);
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        List<String> imageUrls = productImageRepository.findAllByProduct_ProductId(product.getProductId())
                .stream()
                .map(ProductImage::getImageUrl)
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

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductResponseDTO convertToDto(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getCategory().getCategoryid(),
                product.getCategory().getName(),
                product.getImageUrl(), // only main image
                null // no additional images in getAllProducts
        );
    }

    public List<ProductResponseDTO> searchProducts(String searchTerm) {
        return productRepository.searchByNameOrManufacturer(searchTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> findProductCategoryId(Long categoryId) {
        return productRepository.findProductCategoryId(categoryId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
