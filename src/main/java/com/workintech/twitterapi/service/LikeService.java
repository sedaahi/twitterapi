package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.LikeRequest;

public interface LikeService {

    void likeTweet(LikeRequest request, String email);

    void dislikeTweet(LikeRequest request, String email);
}