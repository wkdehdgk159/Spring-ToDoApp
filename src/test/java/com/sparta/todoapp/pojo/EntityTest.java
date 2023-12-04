package com.sparta.todoapp.pojo;

import com.sparta.todoapp.dto.CommentRequestDto;
import com.sparta.todoapp.dto.ToDoCardRequestDto;
import com.sparta.todoapp.entity.Comment;
import com.sparta.todoapp.entity.ToDoCard;
import com.sparta.todoapp.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //given
    String username = "Dongha";
    String password = "111111a";

    String title = "테스트용 제목";
    String contents = "테스트용 내용";

    @Test
    @DisplayName("User Entity Test")
    void test1() {
        //when
        User user = new User(username, passwordEncoder.encode(password));

        //then
        assertEquals(user.getUsername(), username, ()-> "지정해 준 username과 실제 username이 다릅니다.");
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
    }

    @Nested
    @DisplayName("ToDoCard Entity Test")
    class Test2 {
        //given
        User user = new User(username, passwordEncoder.encode(password));
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);

        //when
        ToDoCard toDoCard = new ToDoCard(requestDto, user);

        @Order(1)
        @Test
        @DisplayName("Constructor by ToDoCardRequestDto Test")
        void test1() {
            //then
            assertEquals(toDoCard.getTitle(), requestDto.getTitle());
            assertEquals(toDoCard.getContents(), requestDto.getContents());
            assertEquals(toDoCard.getUser(), user);
            assertFalse(toDoCard.isComplete());
        }

        @Order(2)
        @Test
        @DisplayName("update by ToDoCardRequestDto test")
        void test2() {
            //given
            title = "변경된 테스트용 제목";
            contents = "변경된 테스트용 내용";
            requestDto = new ToDoCardRequestDto(title, contents);

            //when
            toDoCard.update(requestDto);

            //then
            assertEquals(toDoCard.getTitle(), requestDto.getTitle());
            assertEquals(toDoCard.getContents(), requestDto.getContents());
        }

        @Order(3)
        @Test
        @DisplayName("completeUpdate Test")
        void test3() {
            //when
            toDoCard.completeUpdate(true);

            //then
            assertTrue(toDoCard.isComplete());
        }

    }

    @Nested
    @DisplayName("Comment Entity Test")
    class Test3 {
        //given
        User user = new User(username, passwordEncoder.encode(password));
        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(title, contents);
        ToDoCard toDoCard = new ToDoCard(requestDto, user);

        String comment_contents = "테스트용 댓글 내용";

        //when
        Comment comment = new Comment(comment_contents, user, toDoCard);

        @Order(1)
        @Test
        @DisplayName("Constructor Test")
        void test1() {
            //then
            assertEquals(comment.getContents(), comment_contents);
            assertEquals(comment.getUser(), user);
            assertEquals(comment.getToDoCard(), toDoCard);
        }

        @Order(2)
        @Test
        @DisplayName("update by CommentRequestDto Test")
        void test2() {
            //given
            CommentRequestDto requestDto1 = new CommentRequestDto();
            requestDto1.setContents("변경된 테스트용 댓글입니다.");

            //when
            comment.update(requestDto1);

            //then
            assertEquals(comment.getContents(), requestDto1.getContents());
        }
    }
}
