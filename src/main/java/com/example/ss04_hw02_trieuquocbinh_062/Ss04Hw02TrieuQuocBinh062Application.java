package com.example.ss04_hw02_trieuquocbinh_062;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Ss04Hw02TrieuQuocBinh062Application {

    public static void main(String[] args) {
        SpringApplication.run(Ss04Hw02TrieuQuocBinh062Application.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
