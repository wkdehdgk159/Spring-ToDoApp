package com.sparta.todoapp.service;

import com.sparta.todoapp.dto.CommentResponseDto;
import com.sparta.todoapp.dto.MessageResponseDto;
import com.sparta.todoapp.dto.ToDoCardRequestDto;
import com.sparta.todoapp.dto.ToDoCardResponseDto;
import com.sparta.todoapp.entity.ToDoCard;
import com.sparta.todoapp.entity.User;
import com.sparta.todoapp.repository.ToDoCardRepository;
import com.sparta.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ToDoCardService {

    private final ToDoCardRepository toDoCardRepository;
    private final UserRepository userRepository;

    public ResponseEntity<ToDoCardResponseDto> createToDoCard(ToDoCardRequestDto requestDto, User user) {

        ToDoCard toDoCard = toDoCardRepository.save(new ToDoCard(requestDto, user));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        return new ResponseEntity<>(new ToDoCardResponseDto(toDoCard), headers, 200);
    }

    public ResponseEntity<ToDoCardResponseDto> getToDoCard(Long id) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ToDoCard toDoCard = toDoCardRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 할일카드를 찾을 수 없습니다."));

        return new ResponseEntity<>(new ToDoCardResponseDto(toDoCard), headers, 200);
    }

    public List<List<ToDoCardResponseDto>> getToDoCards() {
        List<List<ToDoCardResponseDto>> list = new ArrayList<>();
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            List<ToDoCardResponseDto> dtoList = new ArrayList<>();
            List<ToDoCard> toDoCardList = toDoCardRepository.findByUserOrderByCreatedAtDesc(user);
            for (ToDoCard toDoCard : toDoCardList) {
                dtoList.add(new ToDoCardResponseDto(toDoCard));
            }
            list.add(dtoList);
        }
        return list;
    }

    @Transactional
    public ResponseEntity<ToDoCardResponseDto> modifyToDoCard(Long id, ToDoCardRequestDto requestDto, User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ToDoCard toDoCard = toDoCardRepository.findById(id).orElseThrow(() ->
                new NullPointerException("해당 할일카드를 찾을 수 없습니다.")
        );

        //username 비교했는데 다르면 exception
        if(!user.getUsername().equals(toDoCard.getUser().getUsername())) {
            return new ResponseEntity<>(new ToDoCardResponseDto("작성자만 삭제/수정할 수 있습니다."), headers, 400);
        }

        toDoCard.update(requestDto);


        return new ResponseEntity<>(new ToDoCardResponseDto(toDoCard), headers, 200);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> modifyComplete(Long id, boolean complete, User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ToDoCard toDoCard = toDoCardRepository.findById(id).orElseThrow(() ->
                new NullPointerException("해당 할일카드를 찾을 수 없습니다.")
        );

        //username 비교했는데 다르면 exception
        if(!user.getUsername().equals(toDoCard.getUser().getUsername())) {
            return new ResponseEntity<>(new MessageResponseDto(400,"작성자만 삭제/수정할 수 있습니다."), headers, 400);
        }

        toDoCard.completeUpdate(complete);

        return new ResponseEntity<>(new MessageResponseDto(200, "완료여부 처리가 반영되었습니다."), headers, 200);
    }
}
