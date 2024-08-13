package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.LoginRequestDTO;
import com.example.ecommerce.dto.request.SignUpRequestDTO;
import com.example.ecommerce.dto.response.LoginResponseDTO;
import com.example.ecommerce.dto.response.SignUpResponseDTO;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.Userrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    @Autowired
     private Userrepo userrepo;
    public SignUpResponseDTO singUp(SignUpRequestDTO requestDTO){
        SignUpResponseDTO signUpResponseDTO = new SignUpResponseDTO();
        if(requestDTO.getUsername().isEmpty() || requestDTO.getFirstname().isEmpty() || requestDTO.getEmail().isEmpty() ||
            requestDTO.getLastname().isEmpty()){
            signUpResponseDTO.setMessage("Missing Data");
            signUpResponseDTO.setStatusCode(-2l);
            return signUpResponseDTO;
        }
        if(requestDTO.getPasswordhash().length() < 1 || requestDTO.getPasswordhash().length() < 7){
            signUpResponseDTO.setMessage("Your password should be at least 6 characters");
            signUpResponseDTO.setStatusCode(-1l);
            return signUpResponseDTO;
        }
        User selectedUser = userrepo.findByEmail(requestDTO.getEmail());
        if (selectedUser != null){
            signUpResponseDTO.setMessage("Email Already Exists");
            signUpResponseDTO.setStatusCode(-3l);
            return signUpResponseDTO;
        }
        // converting from DTO to Entity to save the entity in database
        User user = new User(
                requestDTO.getFirstname(),
                requestDTO.getLastname(),
                requestDTO.getUsername(),
                requestDTO.getEmail(),
                requestDTO.getPasswordhash()
        );
        userrepo.save(user);
        signUpResponseDTO.setMessage("SignUp Succeeded");
        signUpResponseDTO.setStatusCode(0l); // L 3shsn long
        return signUpResponseDTO;
    }
    public LoginResponseDTO Login(LoginRequestDTO loginRequestDTO)
    {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        if(loginRequestDTO.getUsername()== null ||loginRequestDTO.getPassword()== null){
            loginResponseDTO.setMessage("invalid username or password");
            loginResponseDTO.setStatusCode(-1l);
            return loginResponseDTO;
        }
        User loginUser = userrepo.findByUsernameAndPasswordhash(loginRequestDTO.getUsername(),loginRequestDTO.getPassword());
        if(loginUser==null){
            loginResponseDTO.setMessage("Not Registered");
            loginResponseDTO.setStatusCode(-2L);
            return loginResponseDTO;
        }
        loginResponseDTO.setMessage("Login Success");
        loginResponseDTO.setStatusCode(0l);
        return loginResponseDTO;
    }
}
