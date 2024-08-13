package com.example.ecommerce.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponseDTO {
    private String message;
    private Long statusCode;
}
