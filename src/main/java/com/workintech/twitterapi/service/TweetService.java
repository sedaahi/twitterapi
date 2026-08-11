package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.request.TweetUpdateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;

import java.util.List;

public interface TweetService {

    TweetResponse createTweet(TweetCreateRequest request, String email);

    List<TweetResponse> findByUserId(Long userId);

    TweetResponse findById(Long id);

    TweetResponse updateTweet(Long id, TweetUpdateRequest request, String email);

    void deleteTweet(Long id, String email);

    List<TweetResponse> findAllTweets();
}