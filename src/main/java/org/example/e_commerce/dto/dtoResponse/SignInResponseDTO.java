package org.example.e_commerce.dto.dtoResponse;

import org.example.e_commerce.Entity.User;
import org.example.e_commerce.util.JwtUtil;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class SignInResponseDTO {
    private Map<String, Object> status;
    private Map<String, Object> userDetails;

    public SignInResponseDTO(Map<String, Object> status, Map<String, Object> userDetails) {
        this.status = status;
        this.userDetails = userDetails;
    }

    public static SignInResponseDTO createSuccessfulSignInResponse(User user, JwtUtil jwtUtil) {
        String token = jwtUtil.generateToken(user.getUserid(),user.getUsername(),user.getRole());

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", user.getFirstName());
        userDetails.put("lastName", user.getLastName());
        userDetails.put("role", user.getRole());
        userDetails.put("email", user.getEmail());
        userDetails.put("username", user.getUsername());
        userDetails.put("token", token);

        Map<String, Object> status = new HashMap<>();
        status.put("description", "Login Succeeded");
        status.put("statusCode", HttpStatus.OK.value());

        return new SignInResponseDTO(status, userDetails);
    }

    public static SignInResponseDTO createFailureResponse(String description) {
        Map<String, Object> status = new HashMap<>();
        status.put("description", description);
        status.put("statusCode", HttpStatus.UNAUTHORIZED.value());

        return new SignInResponseDTO(status, null);
    }

    // Getters and Setters
    public Map<String, Object> getStatus() {
        return status;
    }

    public void setStatus(Map<String, Object> status) {
        this.status = status;
    }

    public Map<String, Object> getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(Map<String, Object> userDetails) {
        this.userDetails = userDetails;
    }
}
