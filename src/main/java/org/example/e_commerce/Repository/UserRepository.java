package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

    public User findByUsernameAndPasswordHash(String username , String passwordHash);

    public User findByUsername(String username);

    Long getUserIdByUsername(String username);
}
