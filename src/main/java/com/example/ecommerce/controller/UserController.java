package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.LoginRequestDTO;
import com.example.ecommerce.dto.request.SignUpRequestDTO;
import com.example.ecommerce.dto.response.LoginResponseDTO;
import com.example.ecommerce.dto.response.SignUpResponseDTO;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.Userrepo;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private Userrepo userrepo;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO requestDTO) {

        SignUpResponseDTO response = userService.singUp(requestDTO);

        if (response.getStatusCode() != 0l) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponseDTO = userService.Login(loginRequest);

        if (loginResponseDTO.getStatusCode() != 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginResponseDTO);
        }

        String jwtToken = jwtUtil.generateToken(loginRequest.getUsername());
        loginResponseDTO.setJwtToken(jwtToken);

        return ResponseEntity.ok(loginResponseDTO);
    }


}
