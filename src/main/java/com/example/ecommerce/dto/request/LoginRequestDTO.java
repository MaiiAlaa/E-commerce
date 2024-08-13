package com.example.ecommerce.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotNull(message = "username doesn't exit")
    private String username;

    @NotNull(message = "password doesn't exit")
    private String password;

}
