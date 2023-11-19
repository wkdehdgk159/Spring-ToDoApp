package com.sparta.todoapp.service;

import com.sparta.todoapp.dto.SignupRequestDto;
import com.sparta.todoapp.dto.SignupResponseDto;
import com.sparta.todoapp.dto.ToDoCardResponseDto;
import com.sparta.todoapp.entity.User;
import com.sparta.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<SignupResponseDto> signup(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> checkUsername = userRepository.findByUsername(username);

        if(checkUsername.isPresent()) {
            return new ResponseEntity<>(new SignupResponseDto("중복된 username 입니다.", 400), headers, 400);
        }

        User user = new User(username, password);
        userRepository.save(user);

        return new ResponseEntity<>(new SignupResponseDto("회원가입 완료", 200), headers, 200);
    }
}
