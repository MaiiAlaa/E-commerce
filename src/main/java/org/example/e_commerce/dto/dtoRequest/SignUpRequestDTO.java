package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignUpRequestDTO {

    @NotNull(message = "First name is mandatory")
    @NotBlank(message = "First name is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "First name should contain only alphabetic characters"
    )
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    private String firstname;

    @NotNull(message = "Last name is mandatory")
    @NotBlank(message = "Last name is mandatory")
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Last name should contain only alphabetic characters"
    )
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    private String lastname;

    @NotNull(message = "Username is mandatory")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @NotNull(message = "Username is mandatory")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "Username must not contain spaces"
    )
    @Pattern(
            regexp = ".*[a-zA-Z]+.*",
            message = "Username must contain at least one letter"
    )
    private String username;

    @NotNull(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email should be valid"
    )
    private String email;

    @NotNull(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must have at least 6 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$",
            message = "Password must contain at least one uppercase letter and one special character"
    )
    private String password;

    private String securityquestion;

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

}
