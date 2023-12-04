package com.sparta.todoapp.service;

import com.sparta.todoapp.dto.SignupRequestDto;
import com.sparta.todoapp.dto.SignupResponseDto;
import com.sparta.todoapp.entity.User;
import com.sparta.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("회원가입 성공")
    void test1() {
        //given
        String username = "dongha";
        String password = "12345677";

        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername(username);
        requestDto.setPassword(password);

        //when
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        ResponseEntity<SignupResponseDto> response = userService.signup(requestDto);

        //then
        assertEquals(response.getBody().getMessage(), "회원가입 완료");
        assertEquals(response.getBody().getStatus_code(), 200);
    }

    @Test
    @DisplayName("회원가입 실패")
    void test2() {
        //given
        String username = "dongha";
        String password = "12345677";

        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername(username);
        requestDto.setPassword(password);

        //when
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        ResponseEntity<SignupResponseDto> response = userService.signup(requestDto);

        //then
        assertEquals(response.getBody().getMessage(), "중복된 username 입니다.");
        assertEquals(response.getBody().getStatus_code(), 400);
    }
}
