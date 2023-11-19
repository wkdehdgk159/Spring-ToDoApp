package com.sparta.todoapp.dto;

import com.sparta.todoapp.entity.ToDoCard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ToDoCardResponseDto {
    private Long id;
    private String title;
    private String contents;
    private String username;
    private LocalDateTime createdAt;
    public ToDoCardResponseDto(ToDoCard toDoCard) {
        this.id = toDoCard.getId();
        this.title = toDoCard.getTitle();
        this.contents = toDoCard.getContents();
        this.username = toDoCard.getUser().getUsername();
        this.createdAt = toDoCard.getCreatedAt();
    }

    public ToDoCardResponseDto(String contents) {
        this.contents = contents;
    }
}
