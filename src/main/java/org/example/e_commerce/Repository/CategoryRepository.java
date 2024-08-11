package org.example.e_commerce.Repository;
import org.example.e_commerce.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByName(String categoryName);
    Category findById(long id);
}
