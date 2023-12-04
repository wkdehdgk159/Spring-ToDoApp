package com.sparta.todoapp.service;

import com.sparta.todoapp.dto.MessageResponseDto;
import com.sparta.todoapp.dto.ToDoCardRequestDto;
import com.sparta.todoapp.dto.ToDoCardResponseDto;
import com.sparta.todoapp.entity.ToDoCard;
import com.sparta.todoapp.entity.User;
import com.sparta.todoapp.repository.ToDoCardRepository;
import com.sparta.todoapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ToDoCardServiceTest {

    @InjectMocks
    ToDoCardService toDoCardService;

    @Mock
    ToDoCardRepository toDoCardRepository;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("할일카드 생성")
    void test1() {
        //given
        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        String title = "테스트용 할일카드 제목";
        String contents = "테스트용 할일카드 내용";
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);

        //when
        when(toDoCardRepository.save(any())).thenReturn(new ToDoCard(requestDto, user));
        ResponseEntity<ToDoCardResponseDto> response = toDoCardService.createToDoCard(requestDto, user);

        //then
        assertEquals(response.getBody().getTitle(), title);
        assertEquals(response.getBody().getContents(), contents);
        assertEquals(response.getBody().getUsername(), username);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    @DisplayName("할일카드 조회 성공")
    void test2() {
        //given
        Long id = 2L;
        String title = "테스트용 할일카드 제목";
        String contents = "테스트용 할일카드 내용";
        String username = "dongha";

        //무분별한 게터 세터는 안된다고 배웠는데
        //테스트를 위해서는 모든 엔티티에 생성자, 게터, 세터 만들어주는게 좋은지 궁금합니다.
        ToDoCard toDoCard = new ToDoCard();
        toDoCard.setId(id);
        toDoCard.setTitle(title);
        toDoCard.setContents(contents);
        toDoCard.setUser(new User(username, "password"));
        toDoCard.setComplete(false);


        //when
        //테스트를 위해 레포지토리에서 가져온 듯한 엔티티를 반환해 주어야 하는데(테스트하는 메소드의 리턴값에 필요)
        //애초에 테스트를 짜기 좋은 코드가 아니었는지 더 좋은 방법이 있는 건지 궁금합니다.
        when(toDoCardRepository.findById(id)).thenReturn(Optional.of(toDoCard));
        ResponseEntity<ToDoCardResponseDto> response = toDoCardService.getToDoCard(id);

        //then
        assertEquals(response.getBody().getTitle(), title);
        assertEquals(response.getBody().getContents(), contents);
        assertEquals(response.getBody().getUsername(), username);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    @DisplayName("할일카드 조회 실패 - id에 해당하는 할일카드 없음")
    void test3() {
        //given
        Long id = 2L;

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            toDoCardService.getToDoCard(id);
        });

        //then
        assertEquals("해당 할일카드를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("할일카드 목록 조회")
    void test4() {
        //given
        User user = new User("dongha", "password");
        User user2 = new User("dongh", "passwor");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        ToDoCard toDoCard = new ToDoCard();
        ToDoCard toDoCard2 = new ToDoCard();
        toDoCard.setUser(user);
        toDoCard2.setUser(user2);

        List<ToDoCard> toDoCardList = new ArrayList<>();
        toDoCardList.add(toDoCard);
        toDoCardList.add(toDoCard2);

        //when
        when(userRepository.findAll()).thenReturn(userList);
        when(toDoCardRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(toDoCardList);
        List<List<ToDoCardResponseDto>> list = toDoCardService.getToDoCards();

        //then
        assertEquals(list.get(0).get(0).getUsername(), user.getUsername());
        assertEquals(list.get(0).size(), 2);
    }

    @Test
    @DisplayName("할일카드 수정 성공")
    void test5() {
        //given
        Long id = 2L;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        String title = "테스트용 할일카드 수정 제목";
        String contents = "테스트용 할일카드 수정 내용";
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);

        ToDoCard toDoCard = new ToDoCard(requestDto, user);

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.of(toDoCard));
        ResponseEntity<ToDoCardResponseDto> response = toDoCardService.modifyToDoCard(id, requestDto, user);

        //then
        assertEquals(response.getBody().getTitle(), title);
        assertEquals(response.getBody().getContents(), contents);
        assertEquals(response.getBody().getUsername(), username);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    @DisplayName("할일카드 수정 실패 - id에 해당하는 할일카드 없음")
    void test6() {
        //given
        Long id = 2L;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        String title = "테스트용 할일카드 수정 제목";
        String contents = "테스트용 할일카드 수정 내용";
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);

        ToDoCard toDoCard = new ToDoCard(requestDto, user);

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            toDoCardService.modifyToDoCard(id, requestDto, user);
        });

        //then
        assertEquals("해당 할일카드를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("할일카드 수정 실패 - 할일카드 작성자와 수정 요청자가 다름")
    void test7() {
        //given
        Long id = 2L;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        String title = "테스트용 할일카드 수정 제목";
        String contents = "테스트용 할일카드 수정 내용";
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);

        ToDoCard toDoCard = new ToDoCard(requestDto, new User("different","password"));

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.of(toDoCard));
        ResponseEntity<ToDoCardResponseDto> response = toDoCardService.modifyToDoCard(id, requestDto, user);

        //then
        assertEquals(response.getBody().getContents(), "작성자만 삭제/수정할 수 있습니다.");
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(400));
    }

    @Test
    @DisplayName("할일카드 완료체크 성공")
    void test8() {
        //given
        Long id = 2L;

        boolean complete = true;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        String title = "테스트용 할일카드 수정 제목";
        String contents = "테스트용 할일카드 수정 내용";
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);

        ToDoCard toDoCard = new ToDoCard(requestDto, user);

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.of(toDoCard));
        ResponseEntity<MessageResponseDto> response = toDoCardService.modifyComplete(id, complete, user);

        //then
        assertEquals(response.getBody().getMessage(), "완료여부 처리가 반영되었습니다.");
        assertEquals(response.getBody().getStatus_code(), 200);
    }

    @Test
    @DisplayName("할일카드 완료체크 실패 - id에 해당하는 할일카드 없음")
    void test9() {
        //given
        Long id = 2L;

        boolean complete = true;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            toDoCardService.modifyComplete(id, complete, user);
        });

        //then
        assertEquals("해당 할일카드를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("할일카드 완료체크 실패 - 할일카드 작성자와 수정 요청자가 다름")
    void test10() {
        //given
        Long id = 2L;

        boolean complete = true;

        String username = "dongha";
        String password = "12345677";
        User user = new User(username, password);

        String title = "테스트용 할일카드 수정 제목";
        String contents = "테스트용 할일카드 수정 내용";
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);

        ToDoCard toDoCard = new ToDoCard(requestDto, new User("different","password"));

        //when
        when(toDoCardRepository.findById(id)).thenReturn(Optional.of(toDoCard));
        ResponseEntity<MessageResponseDto> response = toDoCardService.modifyComplete(id, complete, user);

        //then
        assertEquals(response.getBody().getMessage(), "작성자만 삭제/수정할 수 있습니다.");
        assertEquals(response.getBody().getStatus_code(), 400);
    }


}
