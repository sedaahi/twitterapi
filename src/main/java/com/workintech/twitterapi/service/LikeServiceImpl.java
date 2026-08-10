package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.LikeRequest;
import com.workintech.twitterapi.entity.Like;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.LikeRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public LikeServiceImpl(
            LikeRepository likeRepository,
            TweetRepository tweetRepository,
            UserRepository userRepository
    ) {
        this.likeRepository = likeRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    /**
     *
     → JWT'den kullanıcı bulunur
     → tweet bulunur
     → daha önce like var mı?
     → varsa 400
     → yoksa tweet_like tablosuna kayıt
     */

    @Override
    public void likeTweet(LikeRequest request, String email) {

        User user = findUserByEmail(email);
        Tweet tweet = findTweetById(request.tweetId());

        boolean alreadyLiked =
                likeRepository.existsByUserIdAndTweetId(
                        user.getId(),
                        tweet.getId()
                );

        if (alreadyLiked) {
            throw new TwitterException(
                    "Bu tweet zaten beğenilmiş.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Like like = new Like();
        like.setUser(user);
        like.setTweet(tweet);

        likeRepository.save(like);
    }

    /**
     * POST /dislike
     * → kullanıcı bulunur
     * → tweet bulunur
     * → o kullanıcıya ait like aranır
     * → varsa silinir
     * → yoksa 404
     */
    @Override
    public void dislikeTweet(LikeRequest request, String email) {

        User user = findUserByEmail(email);
        Tweet tweet = findTweetById(request.tweetId());

        Like like = likeRepository
                .findByUserIdAndTweetId(
                        user.getId(),
                        tweet.getId()
                )
                .orElseThrow(() ->
                        new TwitterException(
                                "Bu tweet daha önce beğenilmemiş.",
                                HttpStatus.NOT_FOUND
                        )
                );

        likeRepository.delete(like);
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

    private Tweet findTweetById(Long id) {

        return tweetRepository.findById(id)
                .orElseThrow(() ->
                        new TwitterException(
                                "Tweet bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );
    }
}