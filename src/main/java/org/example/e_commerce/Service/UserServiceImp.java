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
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error saving user due to data integrity issues.", e);
        }
    }

    @Override
    public Optional<User> getUserById(long userid) {
        return userRepository.findById(userid);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(long userid, User user) {
        if (userRepository.existsById(userid)) {
            user.setUserid(userid);
            if (user.getPasswordHash() != null) {
                user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + userid);
        }
    }

    @Override
    public void deleteUser(long userid) {
        userRepository.deleteById(userid);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.map(user -> passwordEncoder.matches(password, user.getPasswordHash())).orElse(false);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}