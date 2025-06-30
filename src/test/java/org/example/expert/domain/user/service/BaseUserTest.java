package org.example.expert.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseUserTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    PasswordEncoder passwordEncoder;

    private static final int USERS_COUNT = 1000000;
    private static final int BATCH_SIZE = 10000;

    private static final String INSERT_SQL = "INSERT INTO users (email, nickname, password, user_role, created_at, modified_at) values (?, ?, ?, ?, ?, ?)";

    private static final String PASSWORD = "Password123!";

    protected void createManyUsers() throws InterruptedException {
        int size = USERS_COUNT / BATCH_SIZE;
        int threadPoolSize = Runtime.getRuntime().availableProcessors();
        String encodePassword = passwordEncoder.encode(PASSWORD);

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            final int batch = i;
            executor.submit(() -> {
                try {
                    batchInsert(batch, encodePassword);
                } finally {
                    System.out.println("Batch " + batch + ": end");
                    latch.countDown();
                }
            });
        }

        // 모든 작업이 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        System.out.println("Create Users : " + count);
    }

    private void batchInsert(int batch, String encodePassword) {
        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int num = batch * BATCH_SIZE + i;

                ps.setString(1, "email" + num + "@example.com");
                ps.setString(2, "name" + num);
                ps.setString(3, encodePassword);
                ps.setString(4, "USER");
                ps.setTimestamp(5, null);
                ps.setTimestamp(6, null);
            }

            @Override
            public int getBatchSize() {
                return BATCH_SIZE;
            }
        });
    }

    protected String getTokenByLogin(String email) throws Exception {
        // given
        SigninRequest requestDto = new SigninRequest(email, PASSWORD);

        // when & then
        String signupAsString = mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(signupAsString).get("bearerToken").asText();
    }

    protected String getEmail() {
        return "email" + BATCH_SIZE + 1 + "@example.com";
    }

    protected String getNickname() {
        return "name" + BATCH_SIZE + 1;
    }


}