package org.example.e_commerce.Controller;

import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.ProductImages;
import org.example.e_commerce.Repository.ProductImagesRepository;
import org.example.e_commerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads/";
    private static final String SERVER_URL = "https://e-commerce-production-e59d.up.railway.app/api/files/";

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("productId") Long productId) {
        try {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + uniqueFileName);
            Files.copy(file.getInputStream(), path);

            // Build the file URL to be stored in the database
            String fileUrl = SERVER_URL + uniqueFileName;

            // Find the product by its ID
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.setImageUrl(fileUrl);  // Save the file URL instead of the file path
                productRepository.save(product);  // Save the product with the updated image URL
            } else {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>("File uploaded successfully: " + fileUrl, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/image/{productId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            String imageUrl = productOpt.get().getImageUrl();
            Path imagePath = Paths.get(imageUrl);

            try {
                byte[] imageBytes = Files.readAllBytes(imagePath);
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.IMAGE_JPEG) // Adjust this depending on the image type
                        .body(imageBytes);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Determine the file's content type
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream"; // Default to binary stream if unknown
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Autowired
    private ProductImagesRepository productImageRepository;
    @PostMapping("/upload-images")
    public ResponseEntity<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("productId") Long productId) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (!productOpt.isPresent()) {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }

            Product product = productOpt.get();
            for (MultipartFile file : files) {
                String uniqueFileName = UUID.randomUUID().toString() ;
                Path path = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.copy(file.getInputStream(), path);
                // Build the file URL to be stored in the database
                String fileUrl = SERVER_URL + uniqueFileName;
                ProductImages productImage = new ProductImages();
                productImage.setProduct(product);
                productImage.setImageUrl(fileUrl);

                productImageRepository.save(productImage);
            }

            return new ResponseEntity<>("Files uploaded successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/images/{productId}")
    public ResponseEntity<List<String>> getImageUrls(@PathVariable Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            List<ProductImages> productImages = productImageRepository.findAllByProduct_ProductId(productId);

            List<String> imageUrls = productImages.stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(imageUrls);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-image/{imageId}")
    public ResponseEntity<String> deleteImageById(@PathVariable Long imageId) {
        Optional<ProductImages> productImageOpt = productImageRepository.findById(imageId);

        if (productImageOpt.isPresent()) {
            productImageRepository.delete(productImageOpt.get());
            return ResponseEntity.ok("Image successfully deleted.");
        } else {
            return new ResponseEntity<>("Image ID not found", HttpStatus.NOT_FOUND);
        }
    }
}
