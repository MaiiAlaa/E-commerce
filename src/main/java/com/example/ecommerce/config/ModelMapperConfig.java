package com.example.ecommerce.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
// convert DTO to entity w el 3aks
    //map from DTO and Entity
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}