package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @Auth AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) LocalDateTime startAt,
            @RequestParam(required = false) LocalDateTime endAt
    ) {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, startAt, endAt));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    /**
     * 제목, 담당자 닉네임, 생성일 범위를 기준으로 할 일을 조회하는 메서드
     *
     * @param page 현재 페이지
     * @param size 한 페이지 당 보여줄 데이터 수
     * @param title 제목
     * @param nickname 담당자 닉네임
     * @param startCreatedAt 생성일 기간의 시작
     * @param endCreatedAt 생성일 기간의 끝
     * @return Page
     */
    @GetMapping("/todos/search")
    public ResponseEntity<Page<TodoSearchResponse>> searchTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) LocalDateTime startCreatedAt,
            @RequestParam(required = false) LocalDateTime endCreatedAt
    ) {
        return ResponseEntity.ok(todoService.searchTodos(page, size, title, nickname, startCreatedAt, endCreatedAt));
    }

    /**
     * cascade 작동을 확인하기위해 임시로 만든 삭제 메서드
     *
     * @param todoId 삭제 대상
     */
    @DeleteMapping("/todos/{todoId}")
    public void deleteTodo(@PathVariable long todoId) {
        todoService.deleteTodo(todoId);
    }
}