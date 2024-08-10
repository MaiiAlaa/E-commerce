package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> getUserById(long userid);
    List<User> getAllUsers();
    User updateUser(long userid, User user);
    void deleteUser(long userid);
    boolean authenticateUser(String username, String password);
}
