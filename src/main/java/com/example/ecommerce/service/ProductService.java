package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.response.SignUpResponseDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private SignUpResponseDTO responseDTO = new SignUpResponseDTO();

    @Autowired
    ProductRepo productRepo;

    @Autowired
    private ModelMapper modelMapper;

    public Product convertToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public ProductDTO convertToDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }
    public SignUpResponseDTO addProduct(ProductDTO productDTO) {
        if(productDTO.getProductName() == null || productDTO.getPrice()==null||productDTO.getDescription()==null||productDTO.getCategoryID() == null||productDTO.getWarrantyPeriod() == null||productDTO.getManufacturer()==null){
            responseDTO.setMessage("Fill the data ");
            responseDTO.setStatusCode(-1l);
            return responseDTO;
        }
        Product productExist = productRepo.findByProductName(productDTO.getProductName()); // check lw name da mawgoud wla la
        if (productExist != null) {
            responseDTO.setMessage("Product Alreday Exist Update if you want");
            responseDTO.setStatusCode(-2l);
            return responseDTO;

        }

        Product productNew = convertToEntity(productDTO);
        productRepo.save(productNew);
        responseDTO.setMessage("Added Successfully");
        responseDTO.setStatusCode(0l);
        return responseDTO;
    }
    public SignUpResponseDTO updateProduct(ProductDTO productDTO) {
        Product productExist = productRepo.findByProductName(productDTO.getProductName()); // 3mlt select lel prouduct by name
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
            productRepo.save(productExist);
            responseDTO.setMessage("product updated");
            responseDTO.setStatusCode(0l);
            return responseDTO;
        }
        responseDTO.setMessage("Product didn't Exist Please Add Product ");
        responseDTO.setStatusCode(-4l);
        return responseDTO;
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<Product> getProductById(Long id){
        return productRepo.findByProductId(id);
    }

    public List<Product>findProductCategoryId(Long categoryId){
        return productRepo.findProductCategoryId(categoryId);
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepo.searchByNameOrManufacturer(searchTerm);
    }

}






