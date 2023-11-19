package com.sparta.todoapp.entity;

import com.sparta.todoapp.dto.ToDoCardRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "todocard")
@NoArgsConstructor
public class ToDoCard extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private boolean complete;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ToDoCard(ToDoCardRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.user = user;
        this.complete = false;
    }

    public void update(ToDoCardRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }

    public void completeUpdate(boolean complete) {
        this.complete = complete;
    }
}
