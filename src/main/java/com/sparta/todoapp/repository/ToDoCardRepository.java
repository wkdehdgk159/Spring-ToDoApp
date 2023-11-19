package com.sparta.todoapp.repository;

import com.sparta.todoapp.entity.ToDoCard;
import com.sparta.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoCardRepository extends JpaRepository<ToDoCard, Long> {
    List<ToDoCard> findByUserOrderByCreatedAtDesc(User user);
}
