package org.example.e_commerce.Controller;

import jakarta.validation.Valid;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Service.UserServiceImp;
import org.example.e_commerce.dto.dtoRequest.SignInRequestDTO;
import org.example.e_commerce.dto.dtoRequest.SignUpRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignInResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
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
                response.put("message", error.getDefaultMessage());  // Ensure the message key is used
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
        userServiceImp.createUser(user);
        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDTO> signIn(@Valid @RequestBody SignInRequestDTO signInRequestDTO) {
        try {
            boolean isAuthenticated = userServiceImp.authenticateUser(signInRequestDTO.getUsername(), signInRequestDTO.getPassword());
            SignInResponseDTO response = new SignInResponseDTO();
            if (isAuthenticated) {
                // Generate token
                String token = jwtUtil.generateToken(signInRequestDTO.getUsername());

                // Log the token (optional, for debugging)
                System.out.println("Generated Token: " + token);

                // Set the response data
                response.setMessage("User authenticated successfully");
                response.setStatusCode(HttpStatus.OK.value());
                response.setToken(token);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Invalid username or password");
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SignInResponseDTO response = new SignInResponseDTO();
            response.setMessage("Internal server error");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/signin")
//    public ResponseEntity<SignInResponseDTO> signIn(@Valid @RequestBody SignInRequestDTO signInRequestDTO) {
//        try {
//            boolean isAuthenticated = userServiceImp.authenticateUser(signInRequestDTO.getUsername(), signInRequestDTO.getPassword());
//            SignInResponseDTO response = new SignInResponseDTO();
//            if (isAuthenticated) {
//                // Generate token
//                String token = jwtUtil.generateToken(signInRequestDTO.getUsername());
//
//                // Log the token (optional, for debugging)
//                System.out.println("Generated Token: " + token);
//
//                // Set the token in the response
//                response.setMessage("User authenticated successfully");
//                response.setToken(token);
//
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                response.setMessage("Invalid username or password");
//                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            SignInResponseDTO response = new SignInResponseDTO();
//            response.setMessage("Internal server error");
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }



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
