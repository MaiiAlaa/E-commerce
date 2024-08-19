package org.example.e_commerce.Controller;

import org.example.e_commerce.Service.CartService;
import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public SignUpResponseDTO AddToCart(@RequestHeader("Authorization") String token, @RequestBody PurchaseRequestDTO.ProductRequestDTO productRequest) {
        return cartService.AddToCart(token, productRequest);
    }

    @PostMapping("/purchase")
    public ResponseEntity<SignUpResponseDTO> purchase(@RequestBody PurchaseRequestDTO request) {
        SignUpResponseDTO response = cartService.purchase(request);
        return ResponseEntity.status(Math.toIntExact(response.getStatusCode())).body(response);
    }
}
