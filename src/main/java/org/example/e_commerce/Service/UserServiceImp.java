package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        try {
            // Hash password before saving
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            System.err.println("Data integrity violation: " + e.getMessage());
            throw new RuntimeException("Error saving user due to data integrity issues.", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> getUserById(long userid) {
        try {
            return userRepository.findById(userid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving user by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all users: " + e.getMessage(), e);
        }
    }

    @Override
    public User updateUser(long userid, User user) {
        try {
            if (userRepository.existsById(userid)) {
                user.setUserid(userid);
                // Optionally hash the password if it's updated
                if (user.getPasswordHash() != null) {
                    user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
                }
                return userRepository.save(user);
            }
            throw new RuntimeException("User not found with ID: " + userid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUser(long userid) {
        try {
            userRepository.deleteById(userid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return passwordEncoder.matches(password, user.getPasswordHash());
        }
        return false;
    }
}
