package org.example.e_commerce.Controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Repository.UserRepository;
import org.example.e_commerce.Service.UserServiceImp;
import org.example.e_commerce.dto.dtoRequest.ForgetPasswordRequestDTO;
import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoRequest.SignInRequestDTO;
import org.example.e_commerce.dto.dtoRequest.SignUpRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignInResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.example.e_commerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO, BindingResult bindingResult) {
        SignUpResponseDTO response = new SignUpResponseDTO();

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                response.setMessage(error.getDefaultMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Check if the username already exists
        if (userServiceImp.getUserByUsername(signUpRequestDTO.getUsername()).isPresent()) {
            response.setMessage("Username already exists");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Check if the email already exists
        if (userServiceImp.getUserByEmail(signUpRequestDTO.getEmail()).isPresent()) {
            response.setMessage("Email already exists");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Create new user and save to the database
        User user = new User();
        user.setFirstName(signUpRequestDTO.getFirstname());
        user.setLastName(signUpRequestDTO.getLastname());
        user.setUsername(signUpRequestDTO.getUsername());
        user.setEmail(signUpRequestDTO.getEmail());
        user.setPasswordHash(signUpRequestDTO.getPassword());
        user.setRole("USER");
        user.setSecurityquestion(signUpRequestDTO.getSecurityquestion());
        userServiceImp.saveUser(user);

        // Return success response
        response.setMessage("User registered successfully");
        response.setStatusCode(HttpStatus.CREATED.value());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Handle HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SignUpResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        SignUpResponseDTO response = new SignUpResponseDTO();
        response.setMessage("Invalid JSON format");
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SignUpResponseDTO> handleGeneralException(Exception ex) {
        SignUpResponseDTO response = new SignUpResponseDTO();
        response.setMessage("An unexpected error occurred. Please try again later.");
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ex.printStackTrace(); // Optional: Log the stack trace for debugging
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDTO> signIn(@RequestBody SignInRequestDTO signInRequestDTO) {
        boolean isAuthenticated = userServiceImp.authenticateUser(signInRequestDTO.getUsername(), signInRequestDTO.getPassword());

        if (isAuthenticated) {
            Optional<User> userOpt = userServiceImp.getUserByUsername(signInRequestDTO.getUsername());
            if (userOpt.isPresent()) {
                SignInResponseDTO response = SignInResponseDTO.createSuccessfulSignInResponse(userOpt.get(), jwtUtil);
                return ResponseEntity.ok(response);
            } else {
                return new ResponseEntity<>(SignInResponseDTO.createFailureResponse("User not found"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(SignInResponseDTO.createFailureResponse("Login Failed"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userServiceImp.getUserById(id);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userServiceImp.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/forgetpassword")
    public ResponseEntity<Map<String, Object>> forgetPassword(@RequestBody ForgetPasswordRequestDTO forgetPasswordRequestDTO) {
        try {
            Optional<User> userOpt = userServiceImp.getUserByUsername(forgetPasswordRequestDTO.getUsername());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Check if the provided security question answer matches
                if (Objects.equals(user.getSecurityquestion(), forgetPasswordRequestDTO.getSecurityquestion())) {
                    user.setPasswordHash(passwordEncoder.encode(forgetPasswordRequestDTO.getNewpassword())); // Encode the new password
                    userServiceImp.updateUser(user.getUserid(), user); // Update user with the new password
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("message", "Password successfully changed");
                    responseBody.put("status", HttpStatus.OK.value());
                    return ResponseEntity.ok(responseBody);
                } else {
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("message", "Security question answered incorrectly");
                    responseBody.put("status", HttpStatus.UNAUTHORIZED.value());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
                }
            } else {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("message", "User not found");
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }
        } catch (Exception e) {
            log.error("Error during password reset process: {}", e.getMessage());
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Internal server error");
            responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }
}



    @PostMapping("/changepass")
    public ResponseEntity<SignUpResponseDTO> changePassword(@Valid @RequestBody SignInRequestDTO signInRequestDTO) {
        SignUpResponseDTO response = userServiceImp.changePassword(signInRequestDTO);

        if (response.getStatusCode() != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

}
