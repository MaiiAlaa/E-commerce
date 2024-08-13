package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductResponseDTO;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private SignUpResponseDTO responseDTO = new SignUpResponseDTO();

    private final ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product convertToEntity(ProductRequestDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public SignUpResponseDTO addProduct(ProductRequestDTO productDTO) {
        if(productDTO.getProductName() == null || productDTO.getPrice()==null||productDTO.getDescription()==null||productDTO.getCategoryID() == null||productDTO.getWarrantyPeriod() == null||productDTO.getManufacturer()==null){
            responseDTO.setMessage("Fill the data ");
            responseDTO.setStatusCode(-1l);
            return responseDTO;
        }
        Product productExist = productRepository.findByProductName(productDTO.getProductName()); // check lw name da mawgoud wla la
        if (productExist != null) {
            responseDTO.setMessage("Product Alreday Exist Update if you want");
            responseDTO.setStatusCode(-2l);
            return responseDTO;

        }

        Product productNew = convertToEntity(productDTO);
        productRepository.save(productNew);
        responseDTO.setMessage("Added Successfully");
        responseDTO.setStatusCode(0l);
        return responseDTO;
    }

    public SignUpResponseDTO updateProduct(ProductRequestDTO productDTO) {
        Product productExist = productRepository.findByProductName(productDTO.getProductName()); // 3mlt select lel prouduct by name
        if (productExist != null) {
//            Long productId = productExist.getProductId();
//            int oldQuantity = productExist.getStockQuantity();
//            productExist = convertToEntity(productDTO);
//            productExist.setProductId(productId);
//            int newQuantity = productDTO.getStockQuantity() + oldQuantity;
//            productDTO.setStockQuantity(newQuantity);
//            double newPrice = productDTO.getPrice();
//            productExist.setPrice(newPrice);
//            productExist.setStockQuantity(newQuantity);
            productExist.setProductName(productDTO.getProductName());
            productExist.setCategoryId(productDTO.getCategoryID());
            productExist.setPrice(productDTO.getPrice());
            productExist.setDescription(productDTO.getDescription());
            productExist.setWarrantyPeriod(productDTO.getWarrantyPeriod());
            productExist.setManufacturer(productDTO.getManufacturer());
            int newQuantity = productDTO.getStockQuantity() + productExist.getStockQuantity();
            productExist.setStockQuantity(newQuantity);
            productRepository.save(productExist);
            responseDTO.setMessage("product updated");
            responseDTO.setStatusCode(0l);
            return responseDTO;
        }
        responseDTO.setMessage("Product didn't Exist Please Add Product ");
        responseDTO.setStatusCode(-4l);
        return responseDTO;
    }

    public ProductResponseDTO getProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return new ProductResponseDTO(
                    product.getProductId(),
                    product.getProductName(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.getDescription(),
                    product.getCategory().getCategoryid(),
                    product.getCategory().getName()
            );
        } else {
            // Handle the case where the product is not found, e.g., throw an exception
            throw new RuntimeException("Product not found with id: " + id);
        }
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchByNameOrManufacturer(searchTerm);
    }


}