package org.example.e_commerce.dto.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
    private String message;
    private int statusCode;
    private List<cartProductDetailsDTO> products;
    private double totalCartPrice;
    private int cartSize;
}
