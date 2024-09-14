package org.example.e_commerce.dto.dtoResponse;

import lombok.Data;

@Data
public class PurchaseResponseDTO {
    private String message;
    private String invoiceNumber;
    private int statusCode;
}
