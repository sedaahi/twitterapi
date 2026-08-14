package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.CommentRequest;
import com.workintech.twitterapi.dto.response.CommentResponse;
import com.workintech.twitterapi.dto.response.UserResponse;
import com.workintech.twitterapi.entity.Comment;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.CommentRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            TweetRepository tweetRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentResponse createComment(
            CommentRequest request,
            String email
    ) {

        User user = findUserByEmail(email);

        Tweet tweet = tweetRepository.findById(request.tweetId())
                .orElseThrow(() ->
                        new TwitterException(
                                "Tweet bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );

        Comment comment = new Comment();

        comment.setContent(request.content());
        comment.setUser(user);
        comment.setTweet(tweet);

        Comment savedComment =
                commentRepository.save(comment);

        return toResponse(savedComment);
    }

    @Override
    public List<CommentResponse> findByTweetId(Long tweetId) {

        if (!tweetRepository.existsById(tweetId)) {
            throw new TwitterException(
                    "Tweet bulunamadı.",
                    HttpStatus.NOT_FOUND
            );
        }

        return commentRepository
                .findByTweetIdOrderByCreatedAtAsc(tweetId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CommentResponse> findByUserId(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new TwitterException(
                    "Kullanıcı bulunamadı.",
                    HttpStatus.NOT_FOUND
            );
        }

        return commentRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CommentResponse updateComment(
            Long id,
            CommentRequest request,
            String email
    ) {

        Comment comment = findCommentById(id);

        checkCommentOwner(comment, email);

        comment.setContent(request.content());

        Comment updatedComment =
                commentRepository.save(comment);

        return toResponse(updatedComment);
    }

    @Override
    public void deleteComment(
            Long id,
            String email
    ) {

        Comment comment = findCommentById(id);

        boolean isCommentOwner =
                comment
                        .getUser()
                        .getEmail()
                        .equals(email);

        /*
         * yorum sahibi → silebilir
         * tweet sahibi → silebilir
         * başka biri   → 403
         */
        boolean isTweetOwner =
                comment
                        .getTweet()
                        .getUser()
                        .getEmail()
                        .equals(email);

        if (!isCommentOwner && !isTweetOwner) {
            throw new TwitterException(
                    "Bu yorumu silme yetkiniz yok.",
                    HttpStatus.FORBIDDEN
            );
        }

        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long id) {

        return commentRepository
                .findById(id)
                .orElseThrow(() ->
                        new TwitterException(
                                "Yorum bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private User findUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new TwitterException(
                                "Kullanıcı bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private void checkCommentOwner(
            Comment comment,
            String email
    ) {

        if (!comment
                .getUser()
                .getEmail()
                .equals(email)) {

            throw new TwitterException(
                    "Bu yorumu güncelleme yetkiniz yok.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private CommentResponse toResponse(
            Comment comment
    ) {

        User user = comment.getUser();

        UserResponse userResponse =
                new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                );

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                userResponse,
                comment.getTweet().getId()
        );
    }
}