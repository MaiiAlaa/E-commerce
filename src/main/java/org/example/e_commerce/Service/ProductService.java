package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductDTO;
import org.example.e_commerce.dto.dtoResponse.ProductsResponseDTO;
import org.example.e_commerce.dto.dtoResponse.ProductDetailsDTO;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.ProductImages;
import org.example.e_commerce.Repository.ProductImagesRepository;
import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.Repository.CategoryRepository;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.example.e_commerce.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
     ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductImagesRepository productImagesRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImagesRepository = productImagesRepository;
    }
     CategoryRepository categoryRepository;
    @Autowired
     ProductImagesRepository productImagesRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ModelMapper modelMapper;
    public Product convertToEntity(ProductRequestDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }


    public SignUpResponseDTO addProduct(ProductRequestDTO productDTO, String token) {
        String role = jwtUtil.extractRole(token);
        SignUpResponseDTO responseDTO = new SignUpResponseDTO();

        if (role.equals("USER")) {
            responseDTO.setMessage("You do not have the necessary permissions to perform this action.");
            responseDTO.setStatusCode(403);
            return responseDTO;
        }


        if (productDTO.getProductName() == null || productDTO.getPrice() == null ||
                productDTO.getDescription() == null || productDTO.getCategoryID() == null ||
                productDTO.getWarrantyPeriod() == null || productDTO.getManufacturer() == null ||
                productDTO.getMainImageUrl() == null) {
            responseDTO.setMessage("Fill the data");
            responseDTO.setStatusCode(-1);
            return responseDTO;
        }

        Product productExist = productRepository.findByProductName(productDTO.getProductName());
        if (productExist != null) {
            responseDTO.setMessage("Product Already Exists. Update if you want");
            responseDTO.setStatusCode(-2);
            return responseDTO;
        }

        Category category = categoryRepository.findById(productDTO.getCategoryID()).orElse(null);
        if (category == null) {
            responseDTO.setMessage("Category not found with id: " + productDTO.getCategoryID());
            responseDTO.setStatusCode(-3);
            return responseDTO;
        }

        Product productNew = modelMapper.map(productDTO, Product.class);
        productNew.setCategory(category);
        productRepository.save(productNew);

        if (productDTO.getImageUrls() != null && !productDTO.getImageUrls().isEmpty()) {
            for (String imageUrl : productDTO.getImageUrls()) {
                ProductImages productImage = new ProductImages();
                productImage.setProduct(productNew);
                productImage.setImageUrl(imageUrl);
                productImagesRepository.save(productImage);
            }
        }

        responseDTO.setMessage("Product added successfully");
        responseDTO.setStatusCode(0);
        return responseDTO;
    }


    public SignUpResponseDTO updateProduct(ProductRequestDTO productDTO, String token) {
        String role = jwtUtil.extractRole(token);
        SignUpResponseDTO responseDTO = new SignUpResponseDTO();

        if (role.equals("USER")) {
            responseDTO.setMessage("You do not have the necessary permissions to perform this action.");
            responseDTO.setStatusCode(403);
            return responseDTO;
        }

        Product productExist = productRepository.findByProductName(productDTO.getProductName());
        if (productExist == null) {
            responseDTO.setMessage("Product didn't exist. Please add product");
            responseDTO.setStatusCode(-4);
            return responseDTO;
        }

        Category category = categoryRepository.findById(productDTO.getCategoryID()).orElse(null);
        if (category == null) {
            responseDTO.setMessage("Category not found with id: " + productDTO.getCategoryID());
            responseDTO.setStatusCode(-3);
            return responseDTO;
        }

        productExist.setCategory(category);
        productExist.setPrice(productDTO.getPrice());
        productExist.setDescription(productDTO.getDescription());
        productExist.setWarrantyPeriod(productDTO.getWarrantyPeriod());
        productExist.setManufacturer(productDTO.getManufacturer());
        productExist.setStockQuantity(productDTO.getStockQuantity());

        productRepository.save(productExist);

        if (productDTO.getImageUrls() != null && !productDTO.getImageUrls().isEmpty()) {
            for (String imageUrl : productDTO.getImageUrls()) {
                ProductImages productImage = new ProductImages();
                productImage.setProduct(productExist);
                productImage.setImageUrl(imageUrl);
                productImagesRepository.save(productImage);
            }
        }

        responseDTO.setMessage("Product updated successfully");
        responseDTO.setStatusCode(0);
        return responseDTO;
    }

    public SignUpResponseDTO deleteProduct(Long id, String token) {
        String role = jwtUtil.extractRole(token);
        SignUpResponseDTO responseDTO = new SignUpResponseDTO();

        if (role.equals("USER")) {
            responseDTO.setMessage("You do not have the necessary permissions to perform this action.");
            responseDTO.setStatusCode(403);
            return responseDTO;
        }

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            responseDTO.setMessage("Product deleted successfully");
            responseDTO.setStatusCode(0);
        } else {
            responseDTO.setMessage("Product not found with id: " + id);
            responseDTO.setStatusCode(-1);
        }

        return responseDTO;
    }

  
    public ProductsResponseDTO getProductById(Long id) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return new ProductsResponseDTO(-1L, "Product not found with id: " + id, null);
        }

        List<String> imageUrls = productImagesRepository.findAllByProduct_ProductId(product.getProductId())
                .stream()
                .map(ProductImages::getImageUrl)
                .collect(Collectors.toList());

        ProductDetailsDTO productDTO = new ProductDetailsDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getCategoryid(),
                product.getCategory().getName(),
                product.getImageUrl(), // main image
                product.getDescription(),
                imageUrls // additional images
        );

        return new ProductsResponseDTO(0L, "Product retrieved successfully", Collections.singletonList(productDTO));
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchByNameOrManufacturer(searchTerm);
    }

    public List<Product> findProductByCategoryId(Long categoryId) {
        return productRepository.findProductByCategoryId(categoryId);
    }

    public ProductsResponseDTO getAllProducts() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return new ProductsResponseDTO(-1L, "No products found", null);
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> new ProductDTO(
                        product.getProductId(),
                        product.getProductName(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getCategory().getCategoryid(),
                        product.getCategory().getName(),
                        product.getImageUrl()  // Assuming the main image URL is stored here
                ))
                .collect(Collectors.toList());

        return new ProductsResponseDTO(0L, "Products retrieved successfully", productDTOs);
    }
}
