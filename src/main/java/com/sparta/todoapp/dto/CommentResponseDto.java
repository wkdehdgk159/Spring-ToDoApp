package com.sparta.todoapp.dto;

import com.sparta.todoapp.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {

    private String contents;
    public CommentResponseDto(Comment comment) {
        this.contents = comment.getContents();
    }

    public CommentResponseDto(String s) {
        this.contents = s;
    }
}
