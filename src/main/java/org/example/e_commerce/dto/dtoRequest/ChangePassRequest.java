package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;

@Data
public class ChangePassRequest {
    private String username;
    private String password;
    private String newpassword;
}
