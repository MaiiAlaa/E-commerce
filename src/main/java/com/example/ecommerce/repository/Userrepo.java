package com.example.ecommerce.repository;


import com.example.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 3shan ysahel el t3amol ma3 el databse feh kol el queries CRUD
public interface Userrepo extends JpaRepository<User, Long> {

    public User findByEmail(String email);

    public User findByUsernameAndPasswordhash(String username , String passwordhash);

    public User findByUsername(String username);

    Long getUserIdByUsername(String username);
}
