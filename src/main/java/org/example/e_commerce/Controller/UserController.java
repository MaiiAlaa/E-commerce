package org.example.e_commerce.Controller;
import jakarta.validation.Valid;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Service.UserServiceImp;
import org.example.e_commerce.dto.dtoRequest.ForgetPasswordRequestDTO;
import org.example.e_commerce.dto.dtoRequest.SignInRequestDTO;
import org.example.e_commerce.dto.dtoRequest.SignUpRequestDTO;
import org.example.e_commerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserServiceImp userServiceImp;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        user.setRole("USER");
        user.setSecurityquestion(signUpRequestDTO.getSecurityquestion());
        userServiceImp.saveUser(user);
        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> SignIn(@RequestBody SignInRequestDTO signInRequestDTO) {
        boolean isAuthenticated = userServiceImp.authenticateUser(signInRequestDTO.getUsername(), signInRequestDTO.getPassword());

        if (isAuthenticated) {
            Optional<User> userOpt = userServiceImp.getUserByUsername(signInRequestDTO.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String token = jwtUtil.generateToken(user.getUserid(),user.getUsername());

                Map<String, Object> responseBody = new HashMap<>();
                Map<String, Object> status = new HashMap<>();
                status.put("description", "Login Succeeded");
                status.put("statusCode", HttpStatus.OK.value());

                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("firstName", user.getFirstName());
                userDetails.put("lastName", user.getLastName());
                userDetails.put("email", user.getEmail());
                userDetails.put("username", user.getUsername());
                userDetails.put("role", user.getRole());
                userDetails.put("token", token);

                responseBody.put("status", status);
                responseBody.put("userDetails", userDetails);

                return ResponseEntity.ok(responseBody);
            } else {
                Map<String, Object> responseBody = new HashMap<>();
                Map<String, Object> status = new HashMap<>();
                status.put("description", "User not found");
                status.put("statusCode", HttpStatus.UNAUTHORIZED.value());

                responseBody.put("status", status);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }
        } else {
            Map<String, Object> responseBody = new HashMap<>();
            Map<String, Object> status = new HashMap<>();
            status.put("description", "Login Failed");
            status.put("statusCode", HttpStatus.UNAUTHORIZED.value());

            responseBody.put("status", status);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
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

    @PostMapping("/forgetpassword")
    public ResponseEntity<Map<String, Object>> forgetPassword(@RequestBody ForgetPasswordRequestDTO forgetPasswordRequestDTO) {
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
    }

}


