package com.sparta.todoapp.security;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.todoapp.dto.LoginRequestDto;
import com.sparta.todoapp.dto.MessageResponseDto;
import com.sparta.todoapp.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
//패스워드 아이디 받으면 usernamepasswordauthentication token 만들고 authentication manager를 통해서 검증하는 ~~filter를 상속(jwt 방식을 사용하기 위해 그냥 쓰면 세션 방식)
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        //요 url일 때 필터 적용
        setFilterProcessesUrl("/api/user/login");
    }

    //로그인 시도. 여기는 raw-json 으로 넣어줘야한다.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //body에 있는 username + password json을 dto object 형식으로
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword(), null)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    //로그인 성공시 수행되는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();

        String token = jwtUtil.createToken(username, "ROLE_USER");
        //response 객체 헤더에 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        //json형식으로 토큰값과 성공 메시지 보내기
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = new ObjectMapper().writeValueAsString(new MessageResponseDto(200, "로그인에 성공하셨습니다. 헤더의 토큰 값을 확인해주세요."));
        response.getWriter().write(json);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        jwtUtil.jwtExceptionHandler(response, "회원을 찾을 수 없습니다.");
    }
}
