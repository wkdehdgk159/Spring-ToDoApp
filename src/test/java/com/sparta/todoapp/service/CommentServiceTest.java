package com.sparta.todoapp.service;

import com.sparta.todoapp.dto.CommentRequestDto;
import com.sparta.todoapp.dto.CommentResponseDto;
import com.sparta.todoapp.dto.MessageResponseDto;
import com.sparta.todoapp.entity.Comment;
import com.sparta.todoapp.entity.ToDoCard;
import com.sparta.todoapp.entity.User;
import com.sparta.todoapp.repository.CommentRepository;
import com.sparta.todoapp.repository.ToDoCardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    ToDoCardRepository toDoCardRepository;

    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 생성 성공")
    void test1() {
        //given
        Long id = 2L;

        String contents = "테스트용 댓글 내용";
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.of(new ToDoCard()));
        ResponseEntity<CommentResponseDto> response = commentService.createComment(id, requestDto, user);

        //then
        assertEquals(response.getBody().getContents(), contents);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    @DisplayName("댓글 생성 실패 - id에 해당하는 할일카드 없음")
    void test2() {
        //given
        Long id = 2L;

        String contents = "테스트용 댓글 내용";
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            commentService.createComment(id, requestDto, user);
        });

        //then
        assertEquals("해당 할일카드를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void test3() {
        //given
        Long id = 2L;

        String contents = "테스트용 댓글 수정 내용";
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        Comment comment = new Comment();
        comment.setUser(user);

        //when
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        ResponseEntity<CommentResponseDto> response = commentService.modifyComment(id, requestDto, user);

        //then
        assertEquals(response.getBody().getContents(), contents);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    @DisplayName("댓글 수정 실패 - id에 해당하는 댓글이 없음")
    void test4() {
        //given
        Long id = 2L;

        String contents = "테스트용 댓글 수정 내용";
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        //when
        when(commentRepository.findById(id)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            commentService.modifyComment(id, requestDto, user);
        });

        //then
        assertEquals("해당 댓글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 수정 요청자가 작성자가 아님")
    void test5() {
        //given
        Long id = 2L;

        String contents = "테스트용 댓글 수정 내용";
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        Comment comment = new Comment();
        comment.setUser(new User("다른 유저", "pass"));

        //when
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        ResponseEntity<CommentResponseDto> response = commentService.modifyComment(id, requestDto, user);

        //then
        assertEquals(response.getBody().getContents(), "작성자만 삭제/수정할 수 있습니다.");
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(400));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void test6() {
        //given
        Long id = 2L;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        Comment comment = new Comment();
        comment.setUser(user);

        //when
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        ResponseEntity<MessageResponseDto> response = commentService.deleteComment(id, user);

        //then
        assertEquals(response.getBody().getMessage(), "댓글 삭제에 성공하셨습니다.");
        assertEquals(response.getBody().getStatus_code(), 200);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - id에 해당하는 댓글이 없음")
    void test7() {
        //given
        Long id = 2L;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        //when
        when(commentRepository.findById(id)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            commentService.deleteComment(id, user);
        });

        //then
        assertEquals("해당 댓글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 삭제 요청자가 작성자가 아님")
    void test8() {
        //given
        Long id = 2L;

        String contents = "테스트용 댓글 수정 내용";
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        Comment comment = new Comment();
        comment.setUser(new User("다른 유저", "pass"));

        //when
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        ResponseEntity<MessageResponseDto> response = commentService.deleteComment(id, user);

        //then
        assertEquals(response.getBody().getMessage(), "작성자만 삭제/수정할 수 있습니다.");
        assertEquals(response.getBody().getStatus_code(), 400);
    }
}
