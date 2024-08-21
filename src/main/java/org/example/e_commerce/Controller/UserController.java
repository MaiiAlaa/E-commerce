package org.example.e_commerce.Controller;

import jakarta.validation.Valid;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Service.UserServiceImp;
import org.example.e_commerce.dto.dtoRequest.SignInRequestDTO;
import org.example.e_commerce.dto.dtoRequest.SignUpRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignInResponseDTO;
import org.example.e_commerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserServiceImp userServiceImp;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                response.put("message", error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Check if the user already exists
        if (userServiceImp.getUserByUsername(signUpRequestDTO.getUsername()).isPresent()) {
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (userServiceImp.getUserByEmail(signUpRequestDTO.getEmail()).isPresent()) {
            response.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Convert DTO to Entity
        User user = new User();
        user.setFirstName(signUpRequestDTO.getFirstname());
        user.setLastName(signUpRequestDTO.getLastname());
        user.setUsername(signUpRequestDTO.getUsername());
        user.setEmail(signUpRequestDTO.getEmail());
        user.setPasswordHash(signUpRequestDTO.getPassword());
        user.setRole("USER");
        userServiceImp.saveUser(user);

        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
}
