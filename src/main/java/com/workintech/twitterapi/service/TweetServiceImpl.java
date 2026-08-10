package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.request.TweetUpdateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.dto.response.UserResponse;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetServiceImpl(
            TweetRepository tweetRepository,
            UserRepository userRepository
    ) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TweetResponse createTweet(
            TweetCreateRequest request,
            String email
    ) {

        User user = findUserByEmail(email);

        Tweet tweet = new Tweet();
        tweet.setContent(request.content());
        tweet.setUser(user);

        Tweet savedTweet = tweetRepository.save(tweet);

        return toResponse(savedTweet);
    }

    @Override
    public List<TweetResponse> findByUserId(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new TwitterException(
                                "Kullanıcı bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );

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

        checkTweetOwner(tweet, email);

        tweet.setContent(request.content());

        Tweet updatedTweet = tweetRepository.save(tweet);

        return toResponse(updatedTweet);
    }

    @Override
    public void deleteTweet(Long id, String email) {

        Tweet tweet = findTweetById(id);

        checkTweetOwner(tweet, email);

        tweetRepository.delete(tweet);
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

    //update ve delete sırasında sadece tweet sahibi işlem yapabiliyor
    private void checkTweetOwner(Tweet tweet, String email) {

        if (!tweet.getUser().getEmail().equals(email)) {
            throw new TwitterException(
                    "Bu tweet üzerinde işlem yapma yetkiniz yok.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private TweetResponse toResponse(Tweet tweet) {

        User user = tweet.getUser();

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        return new TweetResponse(
                tweet.getId(),
                tweet.getContent(),
                tweet.getCreatedAt(),
                userResponse
        );
    }
}