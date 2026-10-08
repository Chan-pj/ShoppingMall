package com.example.demo.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String name;
    private String id;
    private String phone;
    private String birth;
    private String email;
}