package org.example.e_commerce.dto.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponseDTO {

    private String message;
    private Long statusCode;



}
