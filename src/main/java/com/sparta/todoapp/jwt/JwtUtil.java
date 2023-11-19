package com.sparta.todoapp.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.todoapp.dto.MessageResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 240 * 60 * 1000L; // 4시간

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    //토큰 생성
    public String createToken(String username, String role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .claim(AUTHORIZATION_KEY, role)//사용자 식별값 ID
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) //만료시간 설정
                        .setIssuedAt(date) //발급일
                        .signWith(key, signatureAlgorithm)//암호화
                        .compact();
    }

    //header에서 JWT 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        //bearerToken이 빈 값이 아니고 우리가 지정해준 토큰 식별자를 가지고 있다면 식별자 띄고 값 가져온다.
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    //토큰 검증
    //https://velog.io/@kimdy0915/Spring-Security-Filter-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC%EB%8A%94-%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%95%A0%EA%B9%8C 아주 굳
    public boolean validateToken(String token, HttpServletResponse response) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            jwtExceptionHandler(response, "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            jwtExceptionHandler(response, "Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            jwtExceptionHandler(response, "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            jwtExceptionHandler(response, "JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }

        return false;
    }

    //토큰 검증 오류 시에 response 객체 수정
    public void jwtExceptionHandler(HttpServletResponse response, String message) {
        log.error(message);
        response.setStatus(400);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(new MessageResponseDto(400, message));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
