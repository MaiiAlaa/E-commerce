package org.example.e_commerce.Controller;
import jakarta.validation.Valid;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Service.UserServiceImp;
import org.example.e_commerce.dto.dtoRequest.SignInRequestDTO;
import org.example.e_commerce.dto.dtoRequest.SignUpRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.example.e_commerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserServiceImp userServiceImp;


    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO, BindingResult bindingResult) {
        SignUpResponseDTO response = new SignUpResponseDTO();

        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                for (FieldError error : bindingResult.getFieldErrors()) {
                    response.setMessage(error.getDefaultMessage());
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());  // Set status code for validation error
                }
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (userServiceImp.getUserByUsername(signUpRequestDTO.getUsername()).isPresent()) {
                response.setMessage("Username already exists");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());  // Set status code for username conflict
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            // Check if the email already exists
            else if (userServiceImp.getUserByEmail(signUpRequestDTO.getEmail()).isPresent()) {
                response.setMessage("Email already exists");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());  // Set status code for email conflict
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            User user = new User();
            user.setFirstName(signUpRequestDTO.getFirstname());
            user.setLastName(signUpRequestDTO.getLastname());
            user.setUsername(signUpRequestDTO.getUsername());
            user.setEmail(signUpRequestDTO.getEmail());
            user.setPasswordHash(signUpRequestDTO.getPassword());
            user.setRole("USER");
            userServiceImp.saveUser(user);

            response.setMessage("User registered successfully");
            response.setStatusCode(HttpStatus.CREATED.value());  // Set status code for successful registration
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        }
        catch (HttpMessageNotReadableException e) {
                // Handle invalid JSON format error
                response.setMessage("Invalid JSON format");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        catch (Exception e) {
            e.printStackTrace();
            response.setMessage("An unexpected error occurred. Please try again later.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());  // Set status code for server error
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
}