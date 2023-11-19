package com.sparta.todoapp.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class SignupResponseDto {
    String message;
    int status_code;

    public SignupResponseDto(String message, int status_code) {
        this.message = message;
        this.status_code = status_code;
    }
}
