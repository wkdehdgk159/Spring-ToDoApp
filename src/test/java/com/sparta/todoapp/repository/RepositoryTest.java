package com.sparta.todoapp.repository;

import com.sparta.todoapp.config.WebSecurityConfig;
import com.sparta.todoapp.entity.Comment;
import com.sparta.todoapp.entity.ToDoCard;
import com.sparta.todoapp.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)
        }
)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ToDoCardRepository toDoCardRepository;

    @Autowired
    private CommentRepository commentRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("UserRepository - 유저 추가")
    void test1() {
        //given
        String username1 = "dongha1";
        String password1 = "password1";

        User user1 = new User(username1, passwordEncoder.encode(password1));

        //when
        User savedUser1 = userRepository.save(user1);

        //then
        assertEquals(savedUser1.getUsername(), user1.getUsername());
        assertTrue(passwordEncoder.matches(password1, savedUser1.getPassword()));
    }

    @Test
    @DisplayName("UserRepository - 유저 이름으로 유저 찾기")
    void test2() {
        //given
        String username1 = "dongha1";
        String password1 = "password1";

        String username2 = "donghas";
        String password2 = "passwords";

        User user1 = new User(username1, passwordEncoder.encode(password1));
        User user2 = new User(username2, passwordEncoder.encode(password2));

        //when
        userRepository.save(user2);

        //then
        assertNotNull(userRepository.findByUsername(username2));
        assertEquals(userRepository.findByUsername(username2).get().getUsername(), user2.getUsername());
        assertEquals(Optional.empty(), userRepository.findByUsername(username1));
    }

    @Test
    @DisplayName("ToDoCardRepository - 할일카드 추가")
    void test3() {
        //given
        String username = "dongha1";
        String password = "password1";
        User user = new User(username, passwordEncoder.encode(password));
        userRepository.save(user);

        String title = "테스트용 할일카드 제목";
        String contents = "테스트용 할일카드 내용";

        ToDoCard toDoCard = new ToDoCard();
        toDoCard.setTitle(title);
        toDoCard.setContents(contents);
        toDoCard.setUser(user);
        toDoCard.setComplete(false);

        //when
        ToDoCard savedToDoCard = toDoCardRepository.save(toDoCard);

        //then
        assertEquals(toDoCard.getTitle(), savedToDoCard.getTitle());
        assertEquals(toDoCard.getContents(), savedToDoCard.getContents());
        assertEquals(toDoCard.getUser(), savedToDoCard.getUser());
    }

    @Test
    @DisplayName("ToDoCardRepository - 유저를 통해 시간 내림차순 할일카드 목록 조회")
    void test4() {
        //given
        String username = "dongha1";
        String password = "password1";
        User user = new User(username, passwordEncoder.encode(password));
        userRepository.save(user);

        String fastTitle = "테스트용 할일카드 빠른 제목";
        String fastContents = "테스트용 할일카드 빠른 내용";

        ToDoCard toDoCard1 = new ToDoCard();
        toDoCard1.setTitle(fastTitle);
        toDoCard1.setContents(fastContents);
        toDoCard1.setUser(user);
        toDoCard1.setComplete(false);
        toDoCardRepository.save(toDoCard1);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String slowTitle = "테스트용 할일카드 느린 제목";
        String slowContents = "테스트용 할일카드 느린 내용";

        ToDoCard toDoCard2 = new ToDoCard();
        toDoCard2.setTitle(slowTitle);
        toDoCard2.setContents(slowContents);
        toDoCard2.setUser(user);
        toDoCard2.setComplete(false);
        toDoCardRepository.save(toDoCard2);

        //when
        List<ToDoCard> toDoCardList = toDoCardRepository.findByUserOrderByCreatedAtDesc(user);

        //then
        assertEquals(toDoCardList.size(), 2);
        //늦게 넣은 것이 먼저 나와야 할텐데 뭐가 잘못된 걸까요..
        assertEquals(slowTitle, toDoCardList.get(0).getTitle());
        assertEquals(slowContents, toDoCardList.get(0).getContents());
    }

    @Test
    @DisplayName("CommentRepository - 댓글 생성")
    void test5() {
        //given
        String username = "dongha1";
        String password = "password1";
        User user = new User(username, passwordEncoder.encode(password));
        userRepository.save(user);

        String toDoCardTitle = "테스트용 할일카드 제목";
        String toDoCardContents = "테스트용 할일카드 내용";

        ToDoCard toDoCard = new ToDoCard();
        toDoCard.setTitle(toDoCardTitle);
        toDoCard.setContents(toDoCardContents);
        toDoCard.setUser(user);
        toDoCard.setComplete(false);
        toDoCardRepository.save(toDoCard);

        String contents = "테스트용 댓글 내용";
        Comment comment = new Comment(contents, user, toDoCard);

        //when
        Comment savedComment = commentRepository.save(comment);

        //then
        assertEquals(savedComment.getUser(), user);
        assertEquals(savedComment.getToDoCard(), toDoCard);
        assertEquals(savedComment.getContents(), contents);
    }
}
