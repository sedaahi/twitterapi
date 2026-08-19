package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.dto.response.UserResponse;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RetweetRepository retweetRepository;
    private final CommentRepository commentRepository;

    public TweetServiceImpl(
            TweetRepository tweetRepository,
            UserRepository userRepository,
            LikeRepository likeRepository,
            RetweetRepository retweetRepository,
            CommentRepository commentRepository
    ) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.retweetRepository = retweetRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public TweetResponse createTweet(TweetCreateRequest request, String email) {

        User user = findUserByEmail(email); //JWT’den gelen email ile gerçek kullanıcıyı DB’den bul

        Tweet tweet = new Tweet();
        tweet.setContent(request.content());
        tweet.setUser(user);

        Tweet savedTweet = tweetRepository.save(tweet);

        return toResponse(savedTweet, email); //entity’yi direkt dönmek yerine TweetResponse oluştur
    }

    @Override
    public List<TweetResponse> findByUserId(
            Long userId,
            String email
    ) {

        if (!userRepository.existsById(userId)) {
            throw new TwitterException(
                    "Kullanıcı bulunamadı.",
                    HttpStatus.NOT_FOUND
            );
        }

        return tweetRepository.findByUserId(userId)
                .stream()
                .map(tweet -> toResponse(tweet, email))
                .toList();
    }
    @Override
    public List<TweetResponse> findLikedTweetsByUserId(
            Long userId,
            String email
    ) {

        if (!userRepository.existsById(userId)) {
            throw new TwitterException(
                    "Kullanıcı bulunamadı.",
                    HttpStatus.NOT_FOUND
            );
        }

        return likeRepository
                .findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(like -> toResponse(
                        like.getTweet(),
                        email
                ))
                .toList();
    }
    @Override
    public TweetResponse findById(
            Long id,
            String email
    ) {

        Tweet tweet = findTweetById(id);

        return toResponse(tweet, email);
    }

    @Override
    public TweetResponse updateTweet(
            Long id,
            TweetCreateRequest request,
            String email
    ) {

        Tweet tweet = findTweetById(id);

        validateTweetOwner(tweet, email);

        tweet.setContent(request.content());

        Tweet updatedTweet = tweetRepository.save(tweet);

        return toResponse(updatedTweet, email);
    }

    @Transactional //sayesinde de örneğin retweet silinirken bir hata olursa önceki comment/like silmeleri commit edilmez; tamamı geri alınır
    @Override
    public void deleteTweet(Long id, String email) {

        Tweet tweet = findTweetById(id);

        validateTweetOwner(tweet, email); //owner kontrolü

        commentRepository.deleteByTweetId(id); //comment kayıtları silinir
        likeRepository.deleteByTweetId(id); //like kayıtları silinir
        retweetRepository.deleteByTweetId(id); //retweet kayıtları silinir

        tweetRepository.delete(tweet);//tweet silinir
    }

    @Override
    public List<TweetResponse> findAllTweets(String email) {

        return tweetRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(tweet -> toResponse(tweet, email))
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

    private TweetResponse toResponse(
            Tweet tweet,
            String email
    ) {

        // Tweet sahibini UserResponse DTO'suna çevir
        UserResponse userResponse = new UserResponse(
                tweet.getUser().getId(),
                tweet.getUser().getUsername(),
                tweet.getUser().getEmail()
        );

        // Tweetin toplam comment/like/rt sayıları
        long commentCount = commentRepository.countByTweetId(tweet.getId());

        long likeCount = likeRepository.countByTweetId(tweet.getId());

        long retweetCount = retweetRepository.countByTweetId(tweet.getId());

        // JWT'den gelen email ile giriş yapan kullanıcıyı bul
        User currentUser = findUserByEmail(email);

        // Giriş yapan kullanıcı bu tweeti beğenmiş mi?==>Kalp ikonunun dolu/boş görünmesi
        boolean likedByCurrentUser = likeRepository.existsByUserIdAndTweetId(
                currentUser.getId(),
                tweet.getId()
        );

        // Giriş yapan kullanıcının bu tweet için retweet kaydını bul ==>Kullanıcının bu tweeti retweet edip etmediği
        var currentUserRetweet = retweetRepository.findByUserIdAndTweetId(
                currentUser.getId(),
                tweet.getId()
        );

        // Tweet retweet edilmiş mi? Retweet ikonunun aktif olup olmayacağı
        boolean retweetedByCurrentUser = currentUserRetweet.isPresent();

        // Varsa retweet kaydının id'sini al (undo retweet için)
        Long currentUserRetweetId = currentUserRetweet
                .map(retweet -> retweet.getId())
                .orElse(null);

        // Frontend'e dönecek TweetResponse DTO'sunu oluştur
        return new TweetResponse(
                tweet.getId(),
                tweet.getContent(),
                tweet.getCreatedAt(),
                userResponse,
                commentCount,
                likeCount,
                retweetCount,
                likedByCurrentUser,
                retweetedByCurrentUser,
                currentUserRetweetId
        );
    }
}