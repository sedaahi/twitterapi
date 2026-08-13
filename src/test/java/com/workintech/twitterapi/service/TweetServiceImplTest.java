package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.LikeRepository;
import com.workintech.twitterapi.repository.RetweetRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TweetServiceImplTest {

    // Gerçek repository yerine mock repository kullanıyoruz.
    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private RetweetRepository retweetRepository;

    // Mock repository'ler TweetServiceImpl içine otomatik verilir.
    @InjectMocks
    private TweetServiceImpl tweetService;

    @DisplayName("Can create a tweet")
    @Test
    void createTweet() {

        User user = new User();
        user.setId(1L);
        user.setUsername("seda");
        user.setEmail("seda@example.com");

        TweetCreateRequest request =
                new TweetCreateRequest("Test tweeti");

        // Email ile kullanıcı bulunduğunu varsayıyoruz.
        when(userRepository.findByEmail("seda@example.com"))
                .thenReturn(Optional.of(user));

        // save sonrası DB'nin id verdiğini taklit ediyoruz.
        when(tweetRepository.save(any(Tweet.class)))
                .thenAnswer(invocation -> {
                    Tweet tweet = invocation.getArgument(0);
                    tweet.setId(1L);
                    tweet.setCreatedAt(LocalDateTime.now());
                    return tweet;
                });

        // Yeni tweet'in henüz like ve retweet'i yok.
        when(likeRepository.countByTweetId(1L))
                .thenReturn(0L);

        when(retweetRepository.countByTweetId(1L))
                .thenReturn(0L);

        when(retweetRepository.findByUserIdAndTweetId(1L, 1L))
                .thenReturn(Optional.empty());

        TweetResponse response =
                tweetService.createTweet(
                        request,
                        "seda@example.com"
                );

        assertEquals(1L, response.id());
        assertEquals("Test tweeti", response.content());
        assertEquals("seda", response.user().username());
        assertEquals(0L, response.likeCount());
        assertEquals(0L, response.retweetCount());

        assertFalse(response.likedByCurrentUser());
        assertFalse(response.retweetedByCurrentUser());
        assertNull(response.currentUserRetweetId());

        // Tweet gerçekten save edilmeye çalışılmış mı?
        verify(tweetRepository).save(any(Tweet.class));
    }

    @DisplayName("Cannot delete another user's tweet")
    @Test
    void cannotDeleteAnotherUsersTweet() {

        User tweetOwner = new User();
        tweetOwner.setId(1L);
        tweetOwner.setEmail("owner@example.com");

        Tweet tweet = new Tweet();
        tweet.setId(5L);
        tweet.setUser(tweetOwner);

        // Silinmek istenen tweet mevcut.
        when(tweetRepository.findById(5L))
                .thenReturn(Optional.of(tweet));

        // Farklı email ile silmeye çalışınca exception bekliyoruz.
        TwitterException exception = assertThrows(
                TwitterException.class,
                () -> tweetService.deleteTweet(
                        5L,
                        "other@example.com"
                )
        );

        assertEquals(
                "Bu tweet üzerinde işlem yapma yetkiniz yok.",
                exception.getMessage()
        );

        // Yetkisiz kullanıcı olduğu için delete hiç çalışmamalı.
        verify(tweetRepository, never())
                .delete(any(Tweet.class));
    }
}