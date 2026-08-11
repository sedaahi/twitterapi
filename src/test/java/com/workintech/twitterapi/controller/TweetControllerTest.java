package com.workintech.twitterapi.controller;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.dto.response.UserResponse;
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
import com.workintech.twitterapi.security.JwtAuthenticationFilter;

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

    // SecurityConfig bu bean'i istediği için test context'inde mock oluşturuyoruz.
    // Filter zaten addFilters=false nedeniyle request sırasında çalışmayacak.
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
                1L
        );

        // Service sanki tweet'i bulmuş gibi davranır.
        when(tweetService.findById(1L))
                .thenReturn(tweet);

        // GET isteği atılmış gibi controller'ı test ediyoruz.
        mockMvc.perform(
                        get("/tweet/findById")
                                .param("id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test tweeti"))
                .andExpect(jsonPath("$.user.username").value("seda"))
                .andExpect(jsonPath("$.likeCount").value(2))
                .andExpect(jsonPath("$.retweetCount").value(1));

        // Controller doğru service metodunu çağırmış mı?
        verify(tweetService).findById(1L);
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
                0L,
                0L
        );

        // Authentication'dan giriş yapan kullanıcının email'i gelsin.
        when(authentication.getName())
                .thenReturn("seda@example.com");

        // Service createTweet çağrıldığında örnek response dönsün.
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
                .andExpect(jsonPath("$.likeCount").value(0));

        verify(tweetService).createTweet(
                request,
                "seda@example.com"
        );
    }
}