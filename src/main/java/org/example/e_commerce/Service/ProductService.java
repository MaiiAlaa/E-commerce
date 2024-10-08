package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductResponseDTO;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.ProductImages;
import org.example.e_commerce.Repository.ProductImagesRepository;
import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.Repository.CategoryRepository;
import org.example.e_commerce.dto.dtoResponse.ProductsResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (productDTO.getImageUrls() != null && !productDTO.getImageUrls().isEmpty()) {
            for (String imageUrl : productDTO.getImageUrls()) {
                ProductImages productImage = new ProductImages();
                productImage.setProduct(productNew);
                productImage.setImageUrl(imageUrl);
                productImagesRepository.save(productImage);
            }
        }
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
            responseDTO.setMessage("Product updated");
            responseDTO.setStatusCode(0L);
            return responseDTO;
        }

        responseDTO.setMessage("Product didn't Exist. Please Add Product");
        responseDTO.setStatusCode(-4L);
        return responseDTO;
    }

    public Map<String, Object> getProductById(Long id) {
        Map<String, Object> response = new HashMap<>();

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            response.put("statusCode", -1L);
            response.put("message", "Product not found with id: " + id);
            return response;
        }

        List<String> imageUrls = productImagesRepository.findAllByProduct_ProductId(product.getProductId())
                .stream()
                .map(ProductImages::getImageUrl)
                .collect(Collectors.toList());

        ProductResponseDTO productDTO = new ProductResponseDTO(
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

        response.put("statusCode", 0L);
        response.put("message", "Product retrieved successfully");
        response.put("product", productDTO);

        return response;
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchByNameOrManufacturer(searchTerm);
    }
    public List<Product>findProductCategoryId(Long categoryId){
        return productRepository.findProductCategoryId(categoryId);
    }

    public Map<String, Object> getAllProducts() {
        List<Product> products = productRepository.findAll();
        Map<String, Object> response = new HashMap<>();

        if (products.isEmpty()) {
            response.put("statusCode", -1L);
            response.put("message", "No products found");
            return response;
        }

        List<ProductsResponseDTO> productDTOs = products.stream()
                .map(product -> new ProductsResponseDTO(
                        product.getProductId(),
                        product.getProductName(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getCategory().getCategoryid(),
                        product.getCategory().getName(),
                        product.getImageUrl()  // Assuming the main image URL is stored here
                ))
                .collect(Collectors.toList());

        response.put("statusCode", 0L);
        response.put("message", "Products retrieved successfully");
        response.put("products", productDTOs);

        return response;
    }

}
