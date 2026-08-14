package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    long countByTweetId(Long tweetId);

    Optional<Retweet> findByUserIdAndTweetId(Long userId, Long tweetId);

    @Modifying
    @Query("DELETE FROM Retweet r WHERE r.tweet.id = :tweetId")
    void deleteByTweetId(@Param("tweetId") Long tweetId);
}