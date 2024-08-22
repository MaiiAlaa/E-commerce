package org.example.e_commerce.Service;

import jakarta.validation.Valid;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Repository.UserRepository;
import org.example.e_commerce.dto.dtoRequest.SignInRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    private SignUpResponseDTO responseDTO = new SignUpResponseDTO();
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

            // Avoid double encoding the password
            if (user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2a$")) {
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

    public SignUpResponseDTO changePassword(@Valid SignInRequestDTO signInRequestDTO) {
        SignUpResponseDTO responseDTO = new SignUpResponseDTO();

        // Find the user by username
        User user = userRepository.findByUsername(signInRequestDTO.getUsername()).orElse(null);

        if (user == null) {
            responseDTO.setMessage("User not found");
            responseDTO.setStatusCode(-1);
            return responseDTO;
        }

        // Check if the current password matches
        if (passwordEncoder.matches(signInRequestDTO.getPassword(), user.getPasswordHash())) {
            // Encode the new password and set it
            String encodedNewPassword = passwordEncoder.encode(signInRequestDTO.getNewpassword());
            user.setPasswordHash(encodedNewPassword);
            userRepository.save(user); // Save the updated user

            responseDTO.setMessage("Password Changed");
            responseDTO.setStatusCode(0);
        } else {
            responseDTO.setMessage("Password not correct. Forgot password?");
            responseDTO.setStatusCode(-4);
        }

        return responseDTO;
    }


}