package org.example.expert.log.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.entity.User;
import org.example.expert.log.entity.Log;
import org.example.expert.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "log")
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLogWithManager(Boolean success, User user, Long todoId, String message) {
        Log saveLog = new Log(success, message, todoId, user);
        logRepository.save(saveLog);

        log.info(message);
    }
}