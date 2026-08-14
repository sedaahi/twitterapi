package com.workintech.twitterapi.controller;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.dto.response.UserResponse;
import com.workintech.twitterapi.security.JwtAuthenticationFilter;
import com.workintech.twitterapi.service.TweetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TweetController.class)

// Bu testte Security/JWT filtresini değil,
// sadece controller'ın request-response davranışını test ediyoruz.
@AutoConfigureMockMvc(addFilters = false)
class TweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Gerçek service çalışmaz, davranışını biz belirleriz.
    @MockitoBean
    private TweetService tweetService;

    // Controller Authentication parametresi kullandığı için mockluyoruz.
    @MockitoBean
    private Authentication authentication;

    // SecurityConfig bu bean'i istediği için mock oluşturuyoruz.
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @DisplayName("Can find tweet by id")
    @Test
    void findById() throws Exception {

        UserResponse user = new UserResponse(
                1L,
                "seda",
                "seda@example.com"
        );

        TweetResponse tweet = new TweetResponse(
                1L,
                "Test tweeti",
                LocalDateTime.now(),
                user,
                2L,
                2L,
                1L,
                true,
                true,
                10L
        );
        // JWT'den giriş yapan kullanıcının email'i geliyor.
        when(authentication.getName())
                .thenReturn("seda@example.com");

        // Service artık tweet id + current user email alıyor.
        when(tweetService.findById(
                1L,
                "seda@example.com"
        )).thenReturn(tweet);

        mockMvc.perform(
                        get("/tweet/findById")
                                .param("id", "1")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test tweeti"))
                .andExpect(jsonPath("$.user.username").value("seda"))
                .andExpect(jsonPath("$.likeCount").value(2))
                .andExpect(jsonPath("$.retweetCount").value(1))
                .andExpect(jsonPath("$.likedByCurrentUser").value(true))
                .andExpect(jsonPath("$.retweetedByCurrentUser").value(true))
                .andExpect(jsonPath("$.currentUserRetweetId").value(10));

        verify(tweetService).findById(
                1L,
                "seda@example.com"
        );
    }

    @DisplayName("Can create a tweet")
    @Test
    void createTweet() throws Exception {

        TweetCreateRequest request =
                new TweetCreateRequest("Yeni tweet");

        UserResponse user = new UserResponse(
                1L,
                "seda",
                "seda@example.com"
        );

        TweetResponse response = new TweetResponse(
                1L,
                "Yeni tweet",
                LocalDateTime.now(),
                user,
                0L,     // commentCount
                0L,     // likeCount
                0L,     // retweetCount
                false,  // likedByCurrentUser
                false,  // retweetedByCurrentUser
                null    // currentUserRetweetId
        );

        // Authentication'dan giriş yapan kullanıcının email'i gelsin.
        when(authentication.getName())
                .thenReturn("seda@example.com");

        when(tweetService.createTweet(
                request,
                "seda@example.com"
        )).thenReturn(response);

        mockMvc.perform(
                        post("/tweet")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "content": "Yeni tweet"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Yeni tweet"))
                .andExpect(jsonPath("$.user.username").value("seda"))
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.retweetCount").value(0))
                .andExpect(jsonPath("$.likedByCurrentUser").value(false))
                .andExpect(jsonPath("$.retweetedByCurrentUser").value(false))
                .andExpect(jsonPath("$.currentUserRetweetId").doesNotExist());

        verify(tweetService).createTweet(
                request,
                "seda@example.com"
        );
    }
}