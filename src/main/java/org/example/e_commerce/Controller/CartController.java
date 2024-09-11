package org.example.e_commerce.Controller;

import org.example.e_commerce.Service.CartService;
import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;
import org.example.e_commerce.dto.dtoResponse.CartResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/view")
    public ResponseEntity<CartResponseDTO> viewCart(@RequestHeader("Authorization") String token) {
        CartResponseDTO response = cartService.viewCart(token);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<SignUpResponseDTO> removeProductFromCart(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId) {
        SignUpResponseDTO response = cartService.removeProductFromCart(token, productId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/add")
    public ResponseEntity<SignUpResponseDTO> addToCart(@RequestHeader("Authorization") String token, @RequestBody PurchaseRequestDTO request) {
        SignUpResponseDTO response = cartService.addToCart(token, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/purchase")
    public ResponseEntity<SignUpResponseDTO> purchase(@RequestHeader("Authorization") String token, @RequestBody PurchaseRequestDTO request) {
        SignUpResponseDTO response = cartService.purchase(token, request);  // Pass the token to the service method
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PostMapping("/increase")
    public ResponseEntity<SignUpResponseDTO> increaseProductQuantity(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        SignUpResponseDTO response = cartService.increaseProductQuantity(token, productId, quantity);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/decrease")
    public ResponseEntity<SignUpResponseDTO> decreaseProductQuantity(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        SignUpResponseDTO response = cartService.decreaseProductQuantity(token, productId, quantity);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
