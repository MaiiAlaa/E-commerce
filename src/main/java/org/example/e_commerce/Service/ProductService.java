package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.*;
import org.example.e_commerce.Repository.*;
import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.*;
import org.example.e_commerce.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImagesRepository productImagesRepository;
    private final CartDetailsRepo cartDetailsRepo;
    private final CartRepo cartRepo;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductImagesRepository productImagesRepository,
                          CartDetailsRepo cartDetailsRepo,
                          CartRepo cartRepo,
                          UserRepository userRepository,
                          JwtUtil jwtUtil,
                          ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImagesRepository = productImagesRepository;
        this.cartDetailsRepo = cartDetailsRepo;
        this.cartRepo = cartRepo;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
    }

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

        // Set freshCollection to true and set the freshSince timestamp
        productNew.setFreshCollection(true);
        productNew.setFreshSince(LocalDateTime.now());

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

        Integer cartSize = cartDetailsRepo.findByProduct(product).stream()
                .map(CartDetails::getQuantity)
                .reduce(0, Integer::sum);

        ProductDetailsDTO productDTO = new ProductDetailsDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getCategoryid(),
                product.getCategory().getName(),
                product.getImageUrl(),
                product.getDescription(),
                product.getCategory().getCategoryid(),
                imageUrls,
                cartSize
        );

        return new ProductsResponseDTO(0L, "Product retrieved successfully", Collections.singletonList(productDTO));
    }

    public List<cartProductDetailsDTO> getAllProductsWithCartSize(String token) {
        List<Product> products = productRepository.findAll();

        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUsername(username);

        Map<Long, Integer> productQuantitiesInCart = new HashMap<>();

        userOptional.ifPresent(user -> {
            Long userId = user.getUserid();
            Cart cart = cartRepo.findByUserid(userId).orElse(null);

            if (cart != null) {
                List<CartDetails> cartDetailsList = cartDetailsRepo.findByCart(cart);
                for (CartDetails cartDetails : cartDetailsList) {
                    Long productId = cartDetails.getProduct().getProductId();
                    int quantity = cartDetails.getQuantity();
                    productQuantitiesInCart.put(productId, productQuantitiesInCart.getOrDefault(productId, 0) + quantity);
                }
            }
        });

        return products.stream()
                .map(product -> {
                    int quantityInCart = productQuantitiesInCart.getOrDefault(product.getProductId(), 0);
                    double itemTotalPrice = product.getPrice() * quantityInCart;

                    return new cartProductDetailsDTO(
                            product.getProductId(),
                            product.getProductName(),
                            product.getImageUrl(),
                            product.getPrice(),
                            quantityInCart,
                            itemTotalPrice,
                            quantityInCart
                    );
                })
                .collect(Collectors.toList());
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
                        product.getImageUrl()
                ))
                .collect(Collectors.toList());

        return new ProductsResponseDTO(0L, "Products retrieved successfully", productDTOs);
    }


    public List<Product> getFreshCollections() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // Set cutoff date to 30 days ago
        return productRepository.findFreshCollections(cutoffDate);
    }
}
