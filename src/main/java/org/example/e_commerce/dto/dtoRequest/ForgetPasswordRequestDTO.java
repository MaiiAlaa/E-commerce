package org.example.e_commerce.dto.dtoRequest;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgetPasswordRequestDTO {
    private String username;
    private String securityquestion;
    private String newpassword;
}
