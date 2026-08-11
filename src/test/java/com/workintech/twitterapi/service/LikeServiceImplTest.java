package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.LikeRequest;
import com.workintech.twitterapi.entity.Like;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.LikeRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.workintech.twitterapi.exception.TwitterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    // Gerçek repository yerine sahte repository oluşturuyoruz.
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    // Yukarıdaki mock repository'ler LikeServiceImpl içine otomatik verilir.
    @InjectMocks
    private LikeServiceImpl likeService;

    @DisplayName("Save like when not liked before")
    @Test
    void likeTweet() {

        User user = new User();
        user.setId(1L);
        user.setEmail("seda@example.com");

        Tweet tweet = new Tweet();
        tweet.setId(5L);

        LikeRequest request = new LikeRequest(5L);

        // Email ile kullanıcı arandığında test kullanıcımız dönsün.
        when(userRepository.findByEmail("seda@example.com"))
                .thenReturn(Optional.of(user));

        // Tweet id ile arandığında test tweetimiz dönsün.
        when(tweetRepository.findById(5L))
                .thenReturn(Optional.of(tweet));

        // Bu kullanıcı bu tweet'i daha önce like etmemiş olsun.
        when(likeRepository.existsByUserIdAndTweetId(1L, 5L))
                .thenReturn(false);

        likeService.likeTweet(
                request,
                "seda@example.com"
        );

        // Service gerçekten bir Like kaydetmeye çalıştı mı?
        verify(likeRepository).save(any(Like.class));
    }

    /**
     * User var
     * ↓
     * Tweet var
     * ↓
     * Like zaten var
     * ↓
     * likeTweet çağır
     * ↓
     * TwitterException fırlatılmalı
     */
    @DisplayName("Cannot like the same tweet twice")
    @Test
    void shouldThrowExceptionWhenTweetAlreadyLiked() {

        User user = new User();
        user.setId(1L);
        user.setEmail("seda@example.com");

        Tweet tweet = new Tweet();
        tweet.setId(5L);

        LikeRequest request = new LikeRequest(5L);

        // Kullanıcı ve tweet varmış gibi davran.
        when(userRepository.findByEmail("seda@example.com"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.findById(5L))
                .thenReturn(Optional.of(tweet));

        // Bu kullanıcı tweet'i daha önce like etmiş.
        when(likeRepository.existsByUserIdAndTweetId(1L, 5L))
                .thenReturn(true);

        // Aynı tweet tekrar like edilirse exception bekliyoruz.
        TwitterException exception = assertThrows(
                TwitterException.class,
                () -> likeService.likeTweet(
                        request,
                        "seda@example.com"
                )
        );

        assertEquals(
                "Bu tweet zaten beğenilmiş.",
                exception.getMessage()
        );
    }
}