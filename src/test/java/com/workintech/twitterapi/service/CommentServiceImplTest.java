package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.CommentRequest;
import com.workintech.twitterapi.dto.response.CommentResponse;
import com.workintech.twitterapi.entity.Comment;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.CommentRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @DisplayName("Can create a comment")
    @Test
    void createComment() {
        User user = user(1L, "seda@example.com");
        Tweet tweet = new Tweet();
        tweet.setId(5L);

        when(userRepository.findByEmail("seda@example.com"))
                .thenReturn(Optional.of(user));
        when(tweetRepository.findById(5L))
                .thenReturn(Optional.of(tweet));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    comment.setId(10L);
                    comment.setCreatedAt(LocalDateTime.now());
                    return comment;
                });

        CommentResponse response = commentService.createComment(
                new CommentRequest(5L, "Test yorumu"),
                "seda@example.com"
        );

        assertEquals(10L, response.id());
        assertEquals(5L, response.tweetId());
        assertEquals("Test yorumu", response.content());
    }

    @DisplayName("Cannot update another user's comment")
    @Test
    void cannotUpdateAnotherUsersComment() {
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setUser(user(1L, "owner@example.com"));

        when(commentRepository.findById(10L))
                .thenReturn(Optional.of(comment));

        TwitterException exception = assertThrows(
                TwitterException.class,
                () -> commentService.updateComment(
                        10L,
                        new CommentRequest(5L, "Yeni içerik"),
                        "other@example.com"
                )
        );

        assertEquals(
                "Bu yorumu güncelleme yetkiniz yok.",
                exception.getMessage()
        );
        verify(commentRepository, never()).save(any(Comment.class));
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername("seda");
        user.setEmail(email);
        return user;
    }
}
