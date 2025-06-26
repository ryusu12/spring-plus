package org.example.expert.domain.todo.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QTodoRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo findTodo = queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user)
                .fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchFirst();
        return Optional.ofNullable(findTodo);
    }

    public Page<TodoSearchResponse> findAllWithSearch(String title, String nickname, LocalDateTime startCreatedAt, LocalDateTime endCreatedAt, Pageable pageable) {
        // 동적 쿼리 생성 - BooleanBuilder
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (title != null) {
            booleanBuilder.and(todo.title.contains(title));
        }
        if (nickname != null) {
            booleanBuilder.and(manager.user.nickname.contains(nickname));
        }
        if (startCreatedAt != null) {
            booleanBuilder.and(todo.createdAt.goe(startCreatedAt));
        }
        if (endCreatedAt != null) {
            booleanBuilder.and(todo.createdAt.loe(endCreatedAt));
        }

        List<TodoSearchResponse> todos = queryFactory.select(
                        new QTodoSearchResponse(
                                todo.title,
                                todo.managers.size().longValue(),
                                todo.comments.size().longValue()
                        )).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(todo.createdAt.desc())
                .fetch();

        return new PageImpl<>(todos, pageable, todos.size());
    }
}