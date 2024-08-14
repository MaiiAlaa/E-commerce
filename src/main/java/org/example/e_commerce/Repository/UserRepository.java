package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameAndPasswordHash(String username, String passwordHash);

    Optional<User> findByUsername(String username);

    Optional<Long> getUserIdByUsername(String username);
}
