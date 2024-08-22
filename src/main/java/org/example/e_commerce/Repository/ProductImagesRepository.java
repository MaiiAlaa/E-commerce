package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, Long> {
    List<ProductImages> findAllByProduct_ProductId(Long productId);
    Optional<ProductImages> findByImageUrl(String imageUrl);


}