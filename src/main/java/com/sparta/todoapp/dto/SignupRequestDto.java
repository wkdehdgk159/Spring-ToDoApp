package com.sparta.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    @Pattern(regexp = "^[a-z0-9]{4,10}$")
    //최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)
    private String username;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]{8,15}+$")
    //최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9)
    private String password;
}



