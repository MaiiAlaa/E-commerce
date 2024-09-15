package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.FreshCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreshCollectionRepository extends JpaRepository<FreshCollection, Long> {
}
