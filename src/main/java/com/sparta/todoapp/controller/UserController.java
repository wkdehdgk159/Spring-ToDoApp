package com.sparta.todoapp.controller;

import com.sparta.todoapp.dto.SignupRequestDto;
import com.sparta.todoapp.dto.SignupResponseDto;
import com.sparta.todoapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    //회원가입 기능
    @PostMapping("/user/signup")
    //client에서 html form data로 넘어왔다고 가정 (@ModelAttribute)
    //postman에서 x-www-form-urlencoded에 넣기
    public ResponseEntity<SignupResponseDto> signup(@Valid SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }
}