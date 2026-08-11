package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.request.TweetUpdateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.dto.response.UserResponse;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.LikeRepository;
import com.workintech.twitterapi.repository.RetweetRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RetweetRepository retweetRepository;

    public TweetServiceImpl(
            TweetRepository tweetRepository,
            UserRepository userRepository,
            LikeRepository likeRepository,
            RetweetRepository retweetRepository
    ) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.retweetRepository = retweetRepository;
    }

    @Override
    public TweetResponse createTweet(TweetCreateRequest request, String email) {

        User user = findUserByEmail(email);

        Tweet tweet = new Tweet();
        tweet.setContent(request.content());
        tweet.setUser(user);

        Tweet savedTweet = tweetRepository.save(tweet);

        return toResponse(savedTweet);
    }

    @Override
    public List<TweetResponse> findByUserId(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new TwitterException(
                    "Kullanıcı bulunamadı.",
                    HttpStatus.NOT_FOUND
            );
        }

        return tweetRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TweetResponse findById(Long id) {

        Tweet tweet = findTweetById(id);

        return toResponse(tweet);
    }

    @Override
    public TweetResponse updateTweet(
            Long id,
            TweetUpdateRequest request,
            String email
    ) {

        Tweet tweet = findTweetById(id);

        validateTweetOwner(tweet, email);

        tweet.setContent(request.content());

        Tweet updatedTweet = tweetRepository.save(tweet);

        return toResponse(updatedTweet);
    }

    @Override
    public void deleteTweet(Long id, String email) {

        Tweet tweet = findTweetById(id);

        validateTweetOwner(tweet, email);

        tweetRepository.delete(tweet);
    }

    @Override
    public List<TweetResponse> findAllTweets() {
        return tweetRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Tweet findTweetById(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() ->
                        new TwitterException(
                                "Tweet bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new TwitterException(
                                "Kullanıcı bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    private void validateTweetOwner(Tweet tweet, String email) {

        if (!tweet.getUser().getEmail().equals(email)) {
            throw new TwitterException(
                    "Bu tweet üzerinde işlem yapma yetkiniz yok.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private TweetResponse toResponse(Tweet tweet) {

        UserResponse userResponse = new UserResponse(
                tweet.getUser().getId(),
                tweet.getUser().getUsername(),
                tweet.getUser().getEmail()
        );

        long likeCount =
                likeRepository.countByTweetId(tweet.getId());

        long retweetCount =
                retweetRepository.countByTweetId(tweet.getId());

        return new TweetResponse(
                tweet.getId(),
                tweet.getContent(),
                tweet.getCreatedAt(),
                userResponse,
                likeCount,
                retweetCount
        );
    }
}