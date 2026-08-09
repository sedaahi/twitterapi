package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId); //DELETE /retweet/{id} için
}