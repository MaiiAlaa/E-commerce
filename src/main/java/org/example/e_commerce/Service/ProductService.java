package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductResponseDTO;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.Repository.CategoryRepository;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.e_commerce.Entity.ProductImage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product convertToEntity(ProductRequestDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public ProductResponseDTO addProduct(ProductRequestDTO productDTO) {
        if (productDTO.getProductName() == null || productDTO.getPrice() == null ||
                productDTO.getDescription() == null || productDTO.getCategoryID() == null ||
                productDTO.getWarrantyPeriod() == null || productDTO.getManufacturer() == null) {
            throw new RuntimeException("All fields must be filled.");
        }

        Product productExist = productRepository.findByProductName(productDTO.getProductName());
        if (productExist != null) {
            throw new RuntimeException("Product already exists. Please update if you want.");
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
            throw new RuntimeException("Product doesn't exist. Please add the product.");
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

    public List<ProductResponseDTO> searchProducts(String searchTerm) {
        List<Product> products = productRepository.searchByNameOrManufacturer(searchTerm);
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return convertToDto(product);  // Ensure convertToDto returns a ProductResponseDTO
        } else {
            throw new RuntimeException("Product not found with id: " + id);
        }
    }

    private ProductResponseDTO convertToDto(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getCategory().getCategoryid(),
                product.getCategory().getName()
        );
    }
}