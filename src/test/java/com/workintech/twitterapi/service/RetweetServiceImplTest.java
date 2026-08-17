package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.RetweetRequest;
import com.workintech.twitterapi.entity.Retweet;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.RetweetRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetweetServiceImplTest {

    @Mock
    private RetweetRepository retweetRepository;

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RetweetServiceImpl retweetService;

    @DisplayName("Can retweet a tweet")
    @Test
    void retweet() {
        User user = user(1L, "seda@example.com");
        Tweet tweet = new Tweet();
        tweet.setId(5L);

        when(userRepository.findByEmail("seda@example.com"))
                .thenReturn(Optional.of(user));
        when(tweetRepository.findById(5L))
                .thenReturn(Optional.of(tweet));
        when(retweetRepository.existsByUserIdAndTweetId(1L, 5L))
                .thenReturn(false);

        retweetService.retweet(
                new RetweetRequest(5L),
                "seda@example.com"
        );

        verify(retweetRepository).save(any(Retweet.class));
    }

    @DisplayName("Cannot delete another user's retweet")
    @Test
    void cannotDeleteAnotherUsersRetweet() {
        Retweet retweet = new Retweet();
        retweet.setId(10L);
        retweet.setUser(user(1L, "owner@example.com"));

        when(retweetRepository.findById(10L))
                .thenReturn(Optional.of(retweet));

        TwitterException exception = assertThrows(
                TwitterException.class,
                () -> retweetService.deleteRetweet(
                        10L,
                        "other@example.com"
                )
        );

        assertEquals(
                "Bu retweet'i silme yetkiniz yok.",
                exception.getMessage()
        );
        verify(retweetRepository, never()).delete(any(Retweet.class));
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
