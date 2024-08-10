package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


public class SignInRequestDTO {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    // Getters and Setters
}
