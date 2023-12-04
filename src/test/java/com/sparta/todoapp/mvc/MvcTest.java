package com.sparta.todoapp.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.todoapp.config.WebSecurityConfig;
import com.sparta.todoapp.controller.CommentController;
import com.sparta.todoapp.controller.ToDoCardController;
import com.sparta.todoapp.controller.UserController;
import com.sparta.todoapp.dto.CommentRequestDto;
import com.sparta.todoapp.dto.ToDoCardRequestDto;
import com.sparta.todoapp.entity.User;
import com.sparta.todoapp.security.UserDetailsImpl;
import com.sparta.todoapp.service.CommentService;
import com.sparta.todoapp.service.ToDoCardService;
import com.sparta.todoapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {UserController.class, ToDoCardController.class, CommentController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
public class MvcTest {
    private MockMvc mvc;
    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    ToDoCardService toDoCardService;

    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        String username = "dongha";
        String password = "12345678";
        User testUser = new User(username, password);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("UserController, 회원가입 기능")
    void test1() throws Exception {
        //given
        //Json body가 아닌 Model을 받는 것으로 설계되어서 multivaluemap 사용
        MultiValueMap<String, String> signupRequestForm = new LinkedMultiValueMap<>();
        signupRequestForm.add("username", "dongha");
        signupRequestForm.add("password", "12345678");

        //when - then
        mvc.perform(post("/api/user/signup")
                        .params(signupRequestForm)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ToDoCardController, 할일카드 작성 기능")
    void test2() throws Exception {
        //given
        this.mockUserSetup();
        String title = "테스트용 할일카드 제목";
        String contents = "테스트용 할일카드 내용";

        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(
                title,
                contents
        );

        String toDoCardInfo = objectMapper.writeValueAsString(requestDto);

        //when - then
        mvc.perform(post("/api/todocards")
                        .content(toDoCardInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ToDoCardController, 선택한 할일카드 조회 기능")
    void test3() throws Exception {
        //given
        Long postId = 3L;

        //when - then
        mvc.perform(get("/api/todocards/{id}", postId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ToDoCardController, 할일카드 목록 조회 기능")
    void test4() throws Exception {
        //given

        //when - then
        mvc.perform(get("/api/todocards"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ToDoCardController, 할일카드 수정 기능")
    void test5() throws Exception {
        //given
        this.mockUserSetup();
        String title = "테스트용 할일카드 수정된 제목";
        String contents = "테스트용 할일카드 수정된 내용";
        Long postId = 2L;

        ToDoCardRequestDto requestDto = new ToDoCardRequestDto(
                title,
                contents
        );

        String toDoCardInfo = objectMapper.writeValueAsString(requestDto);

        //when - then
        mvc.perform(put("/api/todocards/{id}", postId)
                        .content(toDoCardInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("ToDoCardController, 할일카드 완료처리 기능")
    void test6() throws Exception {
        //given
        this.mockUserSetup();
        Long postId = 4L;
        boolean complete = true;

        //when - then
        mvc.perform(put("/api/todocards/{id}/{complete}", postId, complete)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("CommentController, 댓글 작성 기능")
    void test7() throws Exception {
        //given
        this.mockUserSetup();
        String contents = "테스트용 댓글 내용";
        Long postId = 4L;
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String commentInfo = objectMapper.writeValueAsString(requestDto);

        //when - then
        mvc.perform(post("/api/todocards/{id}/comments", postId)
                        .content(commentInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("CommentController, 댓글 수정 기능")
    void test8() throws Exception {
        //given
        this.mockUserSetup();
        String contents = "테스트용 수정된 댓글 내용";
        Long commentId = 2L;
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContents(contents);

        String commentInfo = objectMapper.writeValueAsString(requestDto);

        //when - then
        mvc.perform(put("/api/todocards/comments/{id}", commentId)
                        .content(commentInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("CommentController, 댓글 삭제 기능")
    void test9() throws Exception {
        //given
        this.mockUserSetup();
        Long commentId = 2L;

        //when - then
        mvc.perform(delete("/api/todocards/comments/{id}", commentId)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}
