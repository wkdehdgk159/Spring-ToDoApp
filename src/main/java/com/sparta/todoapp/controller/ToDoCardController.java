package com.sparta.todoapp.controller;

import com.sparta.todoapp.dto.MessageResponseDto;
import com.sparta.todoapp.dto.ToDoCardRequestDto;
import com.sparta.todoapp.dto.ToDoCardResponseDto;
import com.sparta.todoapp.security.UserDetailsImpl;
import com.sparta.todoapp.service.ToDoCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todocards")
public class ToDoCardController {

    private final ToDoCardService toDoCardService;

    //할일카드 작성 기능 API
    @PostMapping("")
    public ResponseEntity<ToDoCardResponseDto> createToDoCard(@RequestBody ToDoCardRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return toDoCardService.createToDoCard(requestDto, userDetails.getUser());
    }

    //선택한 할일카드 조회 기능 API
    @GetMapping("/{id}")
    public ResponseEntity<ToDoCardResponseDto> getToDoCard(@PathVariable Long id) {
        return toDoCardService.getToDoCard(id);
    }

    //할일카드 목록 조회 기능 API. 모든 유저 다 보여줘야함
    @GetMapping("")
    public List<List<ToDoCardResponseDto>> getToDoCards() {
        return toDoCardService.getToDoCards();
    }

    //선택한 할일카드 수정 기능 API
    @PutMapping("/{id}")
    public ResponseEntity<ToDoCardResponseDto> modifyToDoCard(@PathVariable Long id, @RequestBody ToDoCardRequestDto requestDto,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return toDoCardService.modifyToDoCard(id, requestDto, userDetails.getUser());
    }

    //할일카드 완료 기능 API
    @PutMapping("/{id}/{complete}")
    public ResponseEntity<MessageResponseDto>modifyComplete(@PathVariable Long id, @PathVariable boolean complete, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return toDoCardService.modifyComplete(id, complete, userDetails.getUser());
    }
}
