package org.example.e_commerce.Controller;

import org.example.e_commerce.Service.CartService;
import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<SignUpResponseDTO> addToCart(@RequestHeader("Authorization") String token,
                                                       @RequestBody PurchaseRequestDTO.ProductRequestDTO productRequestDTO) {
        String jwtToken = token.substring(7);
        SignUpResponseDTO responseDTO = cartService.AddToCart(jwtToken, productRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/purchase")
    public ResponseEntity<SignUpResponseDTO> purchase(@RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        SignUpResponseDTO responseDTO = cartService.purchase(purchaseRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}

