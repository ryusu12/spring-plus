package org.example.expert.domain.user.service;

import org.example.expert.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest extends BaseUserTest {

    @DisplayName("유저를 닉네임으로 검색할 때 얼마나 걸리는지 테스트")
    @Test
    public void test_time_findUsersByNickname() throws Exception {
        long startTime = System.currentTimeMillis();

        findUsersByNicknameTest();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("totalTime : " + totalTime);
    }

    private void findUsersByNicknameTest() throws Exception {
        // given
        String email = getEmail();
        String nickname = getNickname();

        // 로그인 토큰 구하기
        String token = getTokenByLogin(email);


        // when
        ResultActions getUsersByNickname = mockMvc.perform(
                get("/users")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .param("nickname", nickname)
        );
        String getProfileAsString = getUsersByNickname.andReturn()
                .getResponse()
                .getContentAsString();

        String data = objectMapper.readTree(getProfileAsString).get(0).toString();
        UserResponse response = objectMapper.readValue(data, UserResponse.class);


        // then
        getUsersByNickname.andExpect(status().isOk());
        assertEquals(nickname, response.getNickname());
        assertEquals(email, response.getEmail());
    }

    @DisplayName("닉네임을 정확히 입력하지 않으면 유저를 찾지 못한다")
    @Test
    public void exception_findUsersByNickname() throws Exception {
        // given
        String email = getEmail();
        String nickname = "name";

        // 로그인 토큰 구하기
        String token = getTokenByLogin(email);


        // when
        ResultActions getUsersByNickname = mockMvc.perform(
                get("/users")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .param("nickname", nickname)
        );
        String getProfileAsString = getUsersByNickname.andReturn()
                .getResponse()
                .getContentAsString();


        // then
        getUsersByNickname.andExpect(status().isOk());
        assertEquals("[]", getProfileAsString);
    }


}