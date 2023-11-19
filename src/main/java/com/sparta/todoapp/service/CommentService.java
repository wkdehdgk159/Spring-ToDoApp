package com.sparta.todoapp.service;

import com.sparta.todoapp.dto.CommentRequestDto;
import com.sparta.todoapp.dto.CommentResponseDto;
import com.sparta.todoapp.dto.MessageResponseDto;
import com.sparta.todoapp.entity.Comment;
import com.sparta.todoapp.entity.ToDoCard;
import com.sparta.todoapp.entity.User;
import com.sparta.todoapp.repository.CommentRepository;
import com.sparta.todoapp.repository.ToDoCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ToDoCardRepository toDoCardRepository;

    public ResponseEntity<CommentResponseDto> createComment(Long id, CommentRequestDto requestDto, User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ToDoCard toDoCard = toDoCardRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 할일카드를 찾을 수 없습니다."));
        Comment comment = new Comment(requestDto.getContents(), user, toDoCard);
        commentRepository.save(comment);

        return new ResponseEntity<>(new CommentResponseDto(comment), headers, 200);
    }

    @Transactional
    public ResponseEntity<CommentResponseDto> modifyComment(Long id, CommentRequestDto requestDto, User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 댓글을 찾을 수 없습니다."));

        if(!validateUser(user, comment)) {
            return new ResponseEntity<>(new CommentResponseDto("작성자만 삭제/수정할 수 있습니다."), headers, 400);
        }

        comment.update(requestDto);

        return new ResponseEntity<>(new CommentResponseDto(comment), headers, 200);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> deleteComment(Long id, User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 댓글을 찾을 수 없습니다."));

        if(!validateUser(user, comment)) {
            return new ResponseEntity<>(new MessageResponseDto(400, "작성자만 삭제/수정할 수 있습니다."), headers, 400);
        }

        commentRepository.deleteById(id);

        return new ResponseEntity<>(new MessageResponseDto(200, "댓글 삭제에 성공하셨습니다."), headers, 200);
    }

    public boolean validateUser(User user, Comment comment) {
        return user.getUsername().equals(comment.getUser().getUsername());
    }
}
